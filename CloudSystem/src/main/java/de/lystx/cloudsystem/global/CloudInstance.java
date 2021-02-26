package de.lystx.cloudsystem.global;

import de.lystx.cloudsystem.cloud.commands.PermsCommand;
import de.lystx.cloudsystem.global.commands.*;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.Updater;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInShutdown;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutGlobalInfo;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutNPCs;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.console.logger.LoggerService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCService;
import de.lystx.cloudsystem.library.service.serverselector.sign.SignService;
import de.lystx.cloudsystem.library.service.util.LogService;
import de.lystx.cloudsystem.library.webserver.WebServer;
import io.vson.elements.object.VsonObject;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

public class CloudInstance extends CloudLibrary {


    public CloudInstance(Type type) {
        super(type);

        this.cloudServices.add(new CommandService(this, "Command", CloudService.Type.MANAGING));
        this.cloudServices.add(new LoggerService(this, "CloudLogger", CloudService.Type.UTIL));
        this.console = new CloudConsole(this.getService(LoggerService.class), this.getService(CommandService.class), System.getProperty("user.name"));

        this.screenPrinter = new CloudScreenPrinter(this.console, this);

        this.cloudServices.add(new FileService(this, "File", CloudService.Type.CONFIG));

        if (type.equals(Type.CLOUDSYSTEM)) {
            this.webServer = new WebServer(this);
            this.webServer.update("", new VsonObject().append("info", "There's nothing to see here").append("version", Updater.getCloudVersion()));
            this.webServer.start();
        }

        this.cloudServices.add(new ConfigService(this, "Config", CloudService.Type.CONFIG));
        this.cloudServices.add(new LogService(this, "Logging", CloudService.Type.UTIL));
        this.cloudServices.add(new StatisticsService(this, "Stats", CloudService.Type.UTIL));

        this.cloudServices.add(new DatabaseService(this, "Database", CloudService.Type.MANAGING));
        this.cloudServices.add(new CloudPlayerService(this, "CloudPlayerService", CloudService.Type.MANAGING));
        this.cloudServices.add(new GroupService(this, "Groups", CloudService.Type.MANAGING));
        this.cloudServices.add(new TemplateService(this, "Templates", CloudService.Type.MANAGING));
        this.cloudServices.add(new ScreenService(this, "Screens", CloudService.Type.MANAGING));
        this.cloudServices.add(new PermissionService(this, "Permissions", CloudService.Type.MANAGING));
        this.cloudServices.add(new SignService(this, "Signs", CloudService.Type.MANAGING));
        this.cloudServices.add(new NPCService(this, "NPCs", CloudService.Type.MANAGING));
        this.cloudServices.add(new EventService(this, "Event", CloudService.Type.MANAGING));

        this.getService(CommandService.class).registerCommand(new ShutdownCommand("shutdown", "Stops the cloudsystem", "exit", "destroy"));
        this.getService(CommandService.class).registerCommand(new HelpCommand("help", "Shows you this message", "?", "whattodo"));
        this.getService(CommandService.class).registerCommand(new ExecuteCommand("execute", "Sends a command to a server", "cmd", "command"));
        this.getService(CommandService.class).registerCommand(new ReloadCommand("reload", "Reloads the network", "rl"));
        this.getService(CommandService.class).registerCommand(new ClearCommand("clear", "Clears the console", "cl"));

        this.getService(CommandService.class).registerCommand(new StopCommand("stop", "Stops a service or group"));
        this.getService(CommandService.class).registerCommand(new InfoCommand("info", "Shows information", "information"));
        this.getService(CommandService.class).registerCommand(new RunCommand("run", "Starts new services", "start"));
        if (this.getType().equals(Type.CLOUDSYSTEM) && !this.getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            this.getService(CommandService.class).registerCommand(new ScreenCommand("screen", "Shows output of services", this.screenPrinter, "sc"));
            this.getService(CommandService.class).registerCommand(new DownloadCommand("download", "Manages spigot versions", "spigot", "bukkit", "install"));
        }

        this.getService(CommandService.class).registerCommand(new UpdateCommand("update", "Updates the cloud"));
        this.getService(CommandService.class).registerCommand(new LogCommand("log", "Logs a server or all"));
        this.getService(CommandService.class).registerCommand(new BackupCommand("backup", "Creates a backup of the current cloud"));
        this.getService(CommandService.class).registerCommand(new TpsCommand("tps", "Lets you view the current tps of the cloud"));

    }


    public boolean autoUpdater() {
        if (this.getService(ConfigService.class).getNetworkConfig().isSetupDone()) {
            if (this.getService(ConfigService.class).getNetworkConfig().isAutoUpdater()) {
                this.getService(CommandService.class).setActive(false);
                if (!Updater.check(this.console)) {
                    this.console.getLogger().sendMessage("INFO", "§2Succesfully downloaded Version §a" + Updater.getNewVersion() + "§2!");
                    System.exit(0);
                } else {
                    console.getLogger().sendMessage("INFO", "§2CloudSystem is §anewest version§2!");
                }
            }
            this.getService(CommandService.class).setActive(true);
            return true;
        }
        return false;
    }


    public void reloadNPCS() {
        this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutNPCs(this.getService(NPCService.class).getNPCConfig(), this.getService(NPCService.class).getDocument()));
    }

    public void reload() {
        try {
            this.getService(StatisticsService.class).getStatistics().add("reloadedCloud");
            this.getService(ServerService.class).setServiceGroups(this.getService(GroupService.class).getGroups());
            this.getService(PermissionService.class).load();
            this.getService(PermissionService.class).loadEntries();
            this.getService(ConfigService.class).reload();
            this.getService(NPCService.class).load();
            this.getService(SignService.class).load();
            this.getService(SignService.class).loadSigns();

            this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutGlobalInfo(
                    this.getService(ConfigService.class).getNetworkConfig(),
                    this.getService(ServerService.class).getServices(),
                    this.getService(PermissionService.class).getPermissionPool(),
                    this.getService(CloudPlayerService.class).getOnlinePlayers(),
                    this.getService(SignService.class).getCloudSigns(),
                    this.getService(SignService.class).getSignLayOut().getDocument()
            ));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }



    public void shutdown() {
        if (this.type.equals(Type.CLOUDSYSTEM) && this.getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            this.getService(CloudNetworkService.class).sendPacket(new PacketPlayInShutdown());
        }
        this.setRunning(false);
        this.getConsole().interrupt();

        if (this.getService() != null) this.getService().stopServices();

        this.getService(StatisticsService.class).save();
        this.getService(LogService.class).save();
        this.getService(SignService.class).save();
        this.getService(NPCService.class).save();
        this.getService(ConfigService.class).save();

        if (this.getService(ModuleService.class) != null) this.getService(ModuleService.class).shutdown();

        this.getService(Scheduler.class).scheduleDelayedTask(() -> {
            try {
                FileUtils.deleteDirectory(this.getService(FileService.class).getDynamicServerDirectory());
            } catch (IOException ignored) {}
        }, 20L);
        if (this.type.equals(Type.CLOUDSYSTEM)) {
            this.getService(Scheduler.class).scheduleDelayedTask(() -> this.getService(CloudNetworkService.class).shutdown(), 60L);
        }
        this.getService(Scheduler.class).scheduleDelayedTask(() -> System.exit(0), 80L);
    }
}
