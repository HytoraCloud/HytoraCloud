package de.lystx.cloudsystem.global;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.global.commands.*;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.module.Module;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.library.service.setup.impl.InstanceChooser;
import de.lystx.cloudsystem.library.service.updater.Updater;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketShutdown;
import de.lystx.cloudsystem.library.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.console.logger.LoggerService;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.util.LogService;
import de.lystx.cloudsystem.library.service.util.NetworkInfo;
import de.lystx.cloudsystem.library.service.webserver.WebServer;
import de.lystx.cloudsystem.receiver.Receiver;
import io.vson.elements.object.VsonObject;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

public class CloudInstance extends CloudLibrary {

    public CloudInstance(CloudType cloudType) {
        super(cloudType);

        this.cloudServices.add(new CommandService(this, "Command", CloudService.CloudServiceType.MANAGING));
        this.cloudServices.add(new LoggerService(this, "CloudLogger", CloudService.CloudServiceType.UTIL));

        this.console = new CloudConsole(this.getService(LoggerService.class), this.getService(CommandService.class), System.getProperty("user.name"));

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
                    } else {
                        CloudSystem cloudSystem = new CloudSystem();
                    }
                }, 15L);
            });
            return;
        }

        this.cloudServices.add(new LogService(this, "Logging", CloudService.CloudServiceType.UTIL));

        this.screenPrinter = new CloudScreenPrinter(this.console, this);

        if (cloudType.equals(CloudType.CLOUDSYSTEM)) {
            this.webServer = new WebServer(this);
            this.webServer.update("", new VsonObject().append("info", "There's nothing to see here").append("version", Updater.getCloudVersion()));
            this.webServer.start();
        }

        this.cloudServices.add(new ConfigService(this, "Config", CloudService.CloudServiceType.CONFIG));

        this.cloudServices.add(new TemplateService(this, "Templates", CloudService.CloudServiceType.MANAGING));
        this.cloudServices.add(new ScreenService(this, "Screens", CloudService.CloudServiceType.MANAGING));
        this.cloudServices.add(new EventService(this, "Event", CloudService.CloudServiceType.MANAGING));

        this.getService(CommandService.class).registerCommand(new ShutdownCommand(this));
        this.getService(CommandService.class).registerCommand(new HelpCommand(this));
        this.getService(CommandService.class).registerCommand(new ExecuteCommand(this));
        this.getService(CommandService.class).registerCommand(new ReloadCommand(this));
        this.getService(CommandService.class).registerCommand(new ClearCommand(this));

        this.getService(CommandService.class).registerCommand(new StopCommand(this));
        this.getService(CommandService.class).registerCommand(new InfoCommand(this));
        this.getService(CommandService.class).registerCommand(new RunCommand(this));

        this.getService(CommandService.class).registerCommand(new ScreenCommand(this.screenPrinter, this));
        this.getService(CommandService.class).registerCommand(new DownloadCommand(this));

        this.getService(CommandService.class).registerCommand(new UpdateCommand(this));
        this.getService(CommandService.class).registerCommand(new LogCommand(this));
        this.getService(CommandService.class).registerCommand(new BackupCommand(this));
        this.getService(CommandService.class).registerCommand(new TpsCommand(this));

    }

    /**
     * Checks if the Cloud is setup
     * if the AutoUpdater is enabled
     * and if it needs an AutoUpdater
     *
     * @return Boolean
     */
    public boolean autoUpdater() {
        if (this.getService(ConfigService.class).getNetworkConfig().isSetupDone()) {
            if (this.getService(ConfigService.class).getNetworkConfig().isAutoUpdater()) {
                this.getService(CommandService.class).setActive(false);
                if (!Updater.check(this.console)) {
                    this.console.getLogger().sendMessage("INFO", "§2Succesfully downloaded Version §a" + Updater.getNewVersion() + "§2!");
                } else {
                    console.getLogger().sendMessage("INFO", "§2CloudSystem is §anewest version§2!");
                }
            }
            this.getService(CommandService.class).setActive(true);
            return true;
        }
        return false;
    }

    /**
     * Reloads all
     */
    public void reload() {

        for (Module module : this.getService(ModuleService.class).getModules()) {
            module.onReload(this);
        }

        try {
            this.getService(ServerService.class).setServiceGroups(this.getService(GroupService.class).getGroups());
            this.getService(ConfigService.class).reload();

            this.getService(CloudNetworkService.class).sendPacket(new PacketOutGlobalInfo(
                    this.getService(ConfigService.class).getNetworkConfig(),
                    this.getService(ServerService.class).getServices(),
                    this.getService(CloudPlayerService.class).getOnlinePlayers()
            ));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shuts down the Cloud
     */
    public void shutdown() {
        this.sendPacket(new PacketShutdown());
        this.setRunning(false);
        this.getConsole().interrupt();

        if (this.getService() != null) {
            this.getService().stopServices();
        }

        this.getService(LogService.class).save();
        this.getService(ConfigService.class).save();

        if (this.getService(ModuleService.class) != null) {
            this.getService(ModuleService.class).shutdown();
        }

        if (this.getService(ModuleService.class) != null) {
            this.getService(StatisticsService.class).getStatistics().add("allCPUUsage", new NetworkInfo().getCPUUsage());

        }
        this.getService(Scheduler.class).scheduleDelayedTask(() -> {
            try {
                FileUtils.deleteDirectory(this.getService(FileService.class).getDynamicServerDirectory());
            } catch (IOException ignored) {}
        }, 20L);
        if (this.cloudType.equals(CloudType.CLOUDSYSTEM)) {
            this.getService(Scheduler.class).scheduleDelayedTask(() -> this.getService(CloudNetworkService.class).shutdown(), 60L);
        }
        this.getService(Scheduler.class).scheduleDelayedTask(() -> System.exit(0), 80L);
    }
}
