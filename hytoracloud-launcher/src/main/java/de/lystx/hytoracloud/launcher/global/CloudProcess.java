package de.lystx.hytoracloud.launcher.global;

import de.lystx.hytoracloud.driver.service.permission.PermissionService;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
//import de.lystx.cloudsystem.global.commands.*;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.DriverParent;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.service.util.Utils;
import de.lystx.hytoracloud.driver.service.util.other.AuthManager;
import de.lystx.hytoracloud.driver.service.module.Module;
import de.lystx.hytoracloud.driver.service.server.impl.TemplateService;
import de.lystx.hytoracloud.launcher.global.impl.setup.InstanceChooser;
import de.lystx.hytoracloud.driver.service.other.Updater;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketShutdown;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.config.stats.StatsService;
import de.lystx.hytoracloud.driver.service.console.CloudConsole;
import de.lystx.hytoracloud.driver.service.console.logger.LoggerService;
import de.lystx.hytoracloud.driver.service.event.DefaultEventService;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.module.ModuleService;
import de.lystx.hytoracloud.driver.service.other.NetworkService;
import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenPrinter;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenService;
import de.lystx.hytoracloud.driver.service.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.DefaultServiceManager;
import de.lystx.hytoracloud.driver.service.util.log.LogService;
import de.lystx.hytoracloud.driver.service.util.minecraft.NetworkInfo;
import de.lystx.hytoracloud.driver.service.webserver.WebServer;
import de.lystx.hytoracloud.launcher.global.commands.*;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Getter
public class CloudProcess extends CloudDriver implements DriverParent {

    protected WebServer webServer;
    protected CloudConsole console;
    protected CloudScreenPrinter screenPrinter;
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
                console.getLogger().sendMessage("INFO", "§7Starting §b" + (instanceChooser.getInstance().equalsIgnoreCase("2") ? "Receiver" : "CloudSystem") + " §7version §a" + Updater.getCloudVersion() + " §7by §bLystx§h...");
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

        this.screenPrinter = new CloudScreenPrinter(this.console, this);

        if (cloudType.equals(CloudType.CLOUDSYSTEM)) {
            this.webServer = new WebServer(this);
            this.webServer.update("", new VsonObject().append("info", "There's nothing to see here").append("version", Updater.getCloudVersion()));
            this.webServer.start();
        }

        CloudDriver.getInstance().getServiceRegistry().registerService(new ConfigService());

        CloudDriver.getInstance().getServiceRegistry().registerService(new TemplateService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new CloudScreenService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new DefaultEventService());

        this.getInstance(CommandService.class).registerCommand(new ShutdownCommand(this));
        this.getInstance(CommandService.class).registerCommand(new HelpCommand(this));
        this.getInstance(CommandService.class).registerCommand(new ReloadCommand(this));
        this.getInstance(CommandService.class).registerCommand(new ClearCommand(this));

        this.getInstance(CommandService.class).registerCommand(new StopCommand(this));
        this.getInstance(CommandService.class).registerCommand(new InfoCommand(this));
        this.getInstance(CommandService.class).registerCommand(new RunCommand(this));

        this.getInstance(CommandService.class).registerCommand(new ScreenCommand(this.screenPrinter, this));
        this.getInstance(CommandService.class).registerCommand(new DownloadCommand(this));

        this.getInstance(CommandService.class).registerCommand(new UpdateCommand(this));
        this.getInstance(CommandService.class).registerCommand(new LogCommand(this));
        this.getInstance(CommandService.class).registerCommand(new BackupCommand(this));
        this.getInstance(CommandService.class).registerCommand(new TpsCommand(this));

    }

    /**
     * Checks if the Cloud is setup
     * if the AutoUpdater is enabled
     * and if it needs an AutoUpdater
     *
     * @return Boolean
     */
    public boolean autoUpdater() {
        if (this.getInstance(ConfigService.class).getNetworkConfig().isSetupDone()) {
            if (this.getInstance(ConfigService.class).getNetworkConfig().isAutoUpdater()) {
                this.getInstance(CommandService.class).setActive(false);
                if (!Updater.check(this.console)) {
                    this.console.getLogger().sendMessage("INFO", "§2Succesfully downloaded Version §a" + Updater.getNewVersion() + "§2!");
                } else {
                    console.getLogger().sendMessage("INFO", "§2CloudSystem is §anewest version§2!");
                }
            }
            this.getInstance(CommandService.class).setActive(true);
            return true;
        }
        return false;
    }

    /**
     * Reloads all
     */
    public void reload() {

        for (Module module : this.getInstance(ModuleService.class).getModules()) {
            module.onReload();
        }

        try {
            CloudDriver.getInstance().sendPacket(new PacketOutGlobalInfo(
                    CloudDriver.getInstance().getNetworkConfig(),
                    CloudDriver.getInstance().getServiceManager().getServiceMap()
            ));
            ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).setServiceGroups(this.getInstance(GroupService.class).getGroups());
            this.getInstance(ConfigService.class).reload();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shuts down the Cloud
     */
    public void shutdown() {
        this.sendPacket(new PacketShutdown());
        ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).setRunning(false);
        this.console.interrupt();

        if (this.getServiceManager() != null) {
            this.getServiceManager().stopServices();
        }

        this.getInstance(LogService.class).save();
        this.getInstance(ConfigService.class).save();

        if (this.getInstance(ModuleService.class) != null) {
            this.getInstance(ModuleService.class).shutdown();
        }

        if (this.getInstance(ModuleService.class) != null) {
            this.getInstance(StatsService.class).getStatistics().add("allCPUUsage", new NetworkInfo().getCPUUsage());

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
