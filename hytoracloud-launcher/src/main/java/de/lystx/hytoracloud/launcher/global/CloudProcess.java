package de.lystx.hytoracloud.launcher.global;

import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutUpdateTabList;
import lombok.Setter;
import utillity.JsonEntity;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.DriverParent;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.Utils;
import utillity.AuthManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.Module;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.TemplateService;
import de.lystx.hytoracloud.launcher.global.setups.InstanceChooser;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketShutdown;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.logger.LoggerService;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.service.DefaultEventService;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.ModuleService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.NetworkService;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputPrinter;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.utils.log.LogService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.webserver.WebServer;
import de.lystx.hytoracloud.launcher.global.commands.*;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Getter
public class CloudProcess extends CloudDriver implements DriverParent {

    /**
     * The webserver
     */
    @Setter
    protected WebServer webServer;

    /**
     * The console
     */
    protected CloudConsole console;

    /**
     * The authmanager for keys
     */
    protected AuthManager authManager;


    public CloudProcess(CloudType cloudType) {
        super(cloudType);

        Utils.clearConsole();
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", this);

        //Services for every instance
        CloudDriver.getInstance().getServiceRegistry().registerService(new CommandService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new LoggerService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new LogService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new DefaultEventService());

        //The console
        this.console = new CloudConsole(this.getInstance(LoggerService.class), this.getInstance(CommandService.class), System.getProperty("user.name"));
        this.authManager = new AuthManager(new File("auth.json"));


        //CHoosing instance
        if (cloudType == CloudType.NONE) {
            this.console.getCommandManager().setActive(false);
            new InstanceChooser().start(this.console, instanceChooser -> {
                if (instanceChooser.isCancelled()) {
                    console.getLogger().sendMessage("ERROR", "§cWish you a good day anyways :(");
                    System.exit(1);
                    return;
                }
                if (instanceChooser.getInstance().equalsIgnoreCase("3")) {
                    console.getLogger().sendMessage("INFO", "§7Starting §aManager§7....");
                } else {
                    console.getLogger().sendMessage("INFO", "§7Starting §b" + (instanceChooser.getInstance().equalsIgnoreCase("2") ? "Receiver" : "CloudSystem") + " §7version §a" + CloudDriver.getInstance().getVersion() + " §7by §bLystx§h...");
                }
                Scheduler.getInstance().scheduleDelayedTask(Utils::clearConsole, 10L);

                console.stop();
                console.interrupt();

                Scheduler.getInstance().scheduleDelayedTask(() -> {
                    this.console.getCommandManager().setActive(true);
                    if (instanceChooser.getInstance().equalsIgnoreCase("2")) {
                        Receiver receiver = new Receiver();
                        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", receiver);
                    } else if (instanceChooser.getInstance().equalsIgnoreCase("3")) {
                        Manager manager = new Manager();
                        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", manager);
                    } else {
                        CloudSystem cloudSystem = new CloudSystem();
                        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", cloudSystem);
                    }
                }, 15L);
            });
        }

        //Registering commands for every instance
        this.getInstance(CommandService.class).registerCommand(new ShutdownCommand(this));
        this.getInstance(CommandService.class).registerCommand(new HelpCommand(this));
        this.getInstance(CommandService.class).registerCommand(new ReloadCommand(this));
        this.getInstance(CommandService.class).registerCommand(new ClearCommand(this));

    }

    @Override
    public ServiceOutputPrinter getScreenPrinter() {
        return new ServiceOutputPrinter();
    }

    /**
     * Shuts down the Cloud
     */
    public void shutdown() {
        super.shutdown();
        this.console.interrupt();
    }
}
