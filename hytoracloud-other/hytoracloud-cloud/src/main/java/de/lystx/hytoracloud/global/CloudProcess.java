package de.lystx.hytoracloud.global;

import de.lystx.hytoracloud.global.commands.ClearCommand;
import de.lystx.hytoracloud.global.commands.HelpCommand;
import de.lystx.hytoracloud.global.commands.ReloadCommand;
import de.lystx.hytoracloud.global.commands.ShutdownCommand;
import de.lystx.hytoracloud.global.manager.Manager;
import lombok.Setter;
import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.DriverParent;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.cloudservices.global.AuthManager;
import de.lystx.hytoracloud.global.setups.InstanceChooser;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.Console;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.logger.LoggerService;
import de.lystx.hytoracloud.driver.cloudservices.global.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputPrinter;
import de.lystx.hytoracloud.driver.cloudservices.cloud.log.LogService;
import de.lystx.hytoracloud.global.webserver.WebServer;
import de.lystx.hytoracloud.launcher.global.commands.*;
import de.lystx.hytoracloud.receiver.Receiver;
import lombok.Getter;

import java.io.File;

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
    protected Console console;

    /**
     * The authmanager for keys
     */
    protected AuthManager authManager;


    public CloudProcess(CloudType cloudType) {
        super(cloudType);

        Utils.clearConsole();
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", this);

        //Services for every instance
        CloudDriver.getInstance().getServiceRegistry().registerService(new LoggerService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new LogService());

        //The console
        this.console = new Console(this.getInstance(LoggerService.class), this.getInstance(CommandService.class), System.getProperty("user.name"));
        this.authManager = new AuthManager(new File("auth.json"));

        //CHoosing instance
        if (cloudType == CloudType.NONE) {
            this.console.getCommandManager().setActive(false);
            new InstanceChooser().start(instanceChooser -> {
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

    public void bootstrap() {

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
