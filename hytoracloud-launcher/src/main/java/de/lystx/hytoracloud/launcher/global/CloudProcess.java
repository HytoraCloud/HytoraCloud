package de.lystx.hytoracloud.launcher.global;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketReload;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.DriverParent;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.utils.utillity.AuthManager;
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

    protected WebServer webServer;
    protected CloudConsole console;
    protected ServiceOutputPrinter screenPrinter;
    protected AuthManager authManager;


    public CloudProcess(CloudType cloudType) {
        super(cloudType);

        Utils.clearConsole();
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", this);

        CloudDriver.getInstance().getServiceRegistry().registerService(new CommandService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new LoggerService());

        this.console = new CloudConsole(this.getInstance(LoggerService.class), this.getInstance(CommandService.class), System.getProperty("user.name"));
        this.authManager = new AuthManager(new File("auth.json"));

        if (cloudType == CloudType.NONE) {
            this.console.getCommandManager().setActive(false);
            new InstanceChooser().start(this.console, instanceChooser -> {
                if (instanceChooser.isCancelled()) {
                    console.getLogger().sendMessage("ERROR", "§cWish you a good day anyways :(");
                    System.exit(1);
                    return;
                }
                console.getLogger().sendMessage("INFO", "§7Starting §b" + (instanceChooser.getInstance().equalsIgnoreCase("2") ? "Receiver" : "CloudSystem") + " §7version §a" + CloudDriver.getInstance().getVersion() + " §7by §bLystx§h...");
                Scheduler.getInstance().scheduleDelayedTask(() -> {
                    for (int i = 0; i < 100; i++) {
                        System.out.println();
                    }
                }, 10L);

                console.stop();
                console.interrupt();

                Scheduler.getInstance().scheduleDelayedTask(() -> {
                    this.console.getCommandManager().setActive(true);
                    if (instanceChooser.getInstance().equalsIgnoreCase("2")) {
                        Receiver receiver = new Receiver();
                        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", receiver);
                    } else {
                        CloudSystem cloudSystem = new CloudSystem();
                        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", cloudSystem);
                    }
                }, 15L);
            });
            return;
        }

        CloudDriver.getInstance().getServiceRegistry().registerService(new LogService());

        this.screenPrinter = new ServiceOutputPrinter(this.console, this);

        if (cloudType.equals(CloudType.CLOUDSYSTEM)) {
            this.webServer = new WebServer(this);
            this.webServer.update("", new JsonEntity().append("info", "There's nothing to see here").append("routes", this.webServer.getRoutes()).append("version", CloudDriver.getInstance().getVersion()));
            this.webServer.start();
        }

        CloudDriver.getInstance().getServiceRegistry().registerService(new ConfigService());

        CloudDriver.getInstance().getServiceRegistry().registerService(new TemplateService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new ServiceOutputService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new DefaultEventService());

        this.getInstance(CommandService.class).registerCommand(new ShutdownCommand(this));
        this.getInstance(CommandService.class).registerCommand(new HelpCommand(this));
        this.getInstance(CommandService.class).registerCommand(new ReloadCommand(this));
        this.getInstance(CommandService.class).registerCommand(new ClearCommand(this));

        this.getInstance(CommandService.class).registerCommand(new StopCommand(this));
        this.getInstance(CommandService.class).registerCommand(new InfoCommand(this));
        this.getInstance(CommandService.class).registerCommand(new RunCommand(this));

        this.getInstance(CommandService.class).registerCommand(new ScreenCommand(this.screenPrinter, this));
        this.getInstance(CommandService.class).registerCommand(new DownloadCommand());

        this.getInstance(CommandService.class).registerCommand(new LogCommand(this));
        this.getInstance(CommandService.class).registerCommand(new TpsCommand(this));

    }

    /**
     * Reloads all
     */
    public void reload() {

        //Reloading all modules
        for (Module module : this.getInstance(ModuleService.class).getModules()) {
            module.onReload();
        }

        try {

            //Sending config and permission pool
            CloudDriver.getInstance().getNetworkConfig().update();
            CloudDriver.getInstance().getPermissionPool().update();

            //Sending network config and services and groups
            CloudDriver.getInstance().sendPacket(new PacketOutGlobalInfo(
                    CloudDriver.getInstance().getNetworkConfig(),
                    CloudDriver.getInstance().getInstance(GroupService.class).getGroups(),
                    CloudDriver.getInstance().getServiceManager().getAllServices()
            ));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //Updating webserver
        CloudDriver.getInstance().getParent().getWebServer().update("players", new JsonEntity().append("players", CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers()));
        CloudDriver.getInstance().getParent().getWebServer().update("services", new JsonEntity().append("services", CloudDriver.getInstance().getServiceManager().getAllServices()));

    }

    /**
     * Shuts down the Cloud
     */
    public void shutdown() {

        for (ICloudService registeredService : CloudDriver.getInstance().getServiceRegistry().getRegisteredServices()) {
            registeredService.save();
        }

        this.sendPacket(new PacketShutdown());
        ((CloudSideServiceManager) CloudDriver.getInstance().getServiceManager()).setRunning(false);
        this.console.interrupt();

        if (this.getServiceManager() != null) {
            this.getServiceManager().stopServices();
        }

        this.getInstance(LogService.class).save();
        this.getInstance(ConfigService.class).save();

        if (this.getInstance(ModuleService.class) != null) {
            this.getInstance(ModuleService.class).shutdown();
        }

        this.getInstance(Scheduler.class).scheduleDelayedTask(() -> {
            try {
                FileUtils.deleteDirectory(this.getInstance(FileService.class).getDynamicServerDirectory());
            } catch (IOException ignored) {}
        }, 20L);
        if (this.getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            this.getInstance(Scheduler.class).scheduleDelayedTask(() -> this.getInstance(NetworkService.class).shutdown(), 60L);
        }
        this.getInstance(Scheduler.class).scheduleDelayedTask(() -> System.exit(0), 80L);
    }
}
