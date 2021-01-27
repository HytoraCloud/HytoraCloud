package de.lystx.cloudsystem;

import de.lystx.cloudsystem.booting.CloudBootingSetupDone;
import de.lystx.cloudsystem.booting.CloudBootingSetupNotDone;
import de.lystx.cloudsystem.commands.*;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.Updater;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutGlobalInfo;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCService;
import de.lystx.cloudsystem.library.service.serverselector.sign.SignService;
import de.lystx.cloudsystem.library.service.util.LogService;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.console.LoggerService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.webserver.WebServer;
import de.lystx.cloudsystem.other.TicksPerSecond;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

@Getter @Setter
public class CloudSystem extends CloudLibrary {

    @Getter
    private static CloudSystem instance;

    private final CloudScreenPrinter screenPrinter;
    private final TicksPerSecond ticksPerSecond;
    public ServerService service;

    public CloudSystem() {
        super();
        instance = this;


        this.ticksPerSecond = new TicksPerSecond(this);
        this.cloudServices.add(new CommandService(this, "Command", CloudService.Type.MANAGING));
        this.cloudServices.add(new LoggerService(this, "CloudLogger", CloudService.Type.UTIL));
        this.console = new CloudConsole(this.getService(LoggerService.class), this.getService(CommandService.class), System.getProperty("user.name"));

        this.screenPrinter = new CloudScreenPrinter(this.console, this);

        this.cloudServices.add(new FileService(this, "File", CloudService.Type.CONFIG));

        this.webServer = new WebServer(this);
        this.webServer.update("", new Document().append("info", "There's nothing to see here").append("version", Updater.getCloudVersion()));
        this.webServer.start();

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
        this.getService(CommandService.class).registerCommand(new EditCommand("edit", "Edits a serverGroup"));
        this.getService(CommandService.class).registerCommand(new StopCommand("stop", "Stops a service or group"));
        this.getService(CommandService.class).registerCommand(new MaintenanceCommand("maintenance", "Manages maintenance of network", "mc"));
        this.getService(CommandService.class).registerCommand(new InfoCommand("info", "Shows information", "information"));
        this.getService(CommandService.class).registerCommand(new CreateCommand("create", "Creates cloudstuff", "add"));
        this.getService(CommandService.class).registerCommand(new PermsCommand("perms", "Manages permissions", "cperms", "permissions"));
        this.getService(CommandService.class).registerCommand(new RunCommand("run", "Starts new services", "start"));
        this.getService(CommandService.class).registerCommand(new ScreenCommand("screen", "Shows output of services", this.screenPrinter, "sc"));
        this.getService(CommandService.class).registerCommand(new PlayerCommand("player", "Manages players on the network", "players"));
        this.getService(CommandService.class).registerCommand(new ModulesCommand("modules", "Manages modules", "pl", "plugins"));
        this.getService(CommandService.class).registerCommand(new DeleteCommand("delete", "Deletes stuff", "remove"));
        this.getService(CommandService.class).registerCommand(new DownloadCommand("download", "Manages spigot versions", "spigot", "bukkit", "install"));

        if (this.getService(ConfigService.class).getNetworkConfig().isSetupDone()) {
            if (this.getService(ConfigService.class).getNetworkConfig().isAutoUpdater()) {
                /*this.getService(CommandService.class).setActive(false);
                if (!Updater.check(this.console)) {
                    this.console.getLogger().sendMessage("INFO", "§2Succesfully downloaded Version §a" + Updater.getSpigotVersion() + "§2!");
                    System.exit(0);
                } else {
                    console.getLogger().sendMessage("INFO", "§2CloudSystem is §anewest version§2!");
                }*/
            }
            this.getService(CommandService.class).setActive(true);
            new CloudBootingSetupDone(this);
        } else {
            new CloudBootingSetupNotDone(this);
        }
    }

    public void reload() {
        try {
            this.getService(PermissionService.class).load();
            this.getService(PermissionService.class).loadEntries();
            this.getService(ConfigService.class).reload();
            this.getService(NPCService.class).load();
            this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutGlobalInfo(
                    this.getService(ConfigService.class).getNetworkConfig(),
                    this.getService().getServices(),
                    this.getService(PermissionService.class).getPermissionPool(),
                    this.getService(CloudPlayerService.class).getOnlinePlayers(),
                    this.getService(StatisticsService.class).getStatistics(),
                    this.getService(SignService.class).getCloudSigns(),
                    this.getService(SignService.class).getSignLayOut().getDocument(),
                    this.getService(NPCService.class).getDocument(),
                    this.getService(NPCService.class).getNPCConfig()
            ));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        this.setRunning(false);
        this.getConsole().interrupt();
        this.getService().stopServices();
        this.getService(StatisticsService.class).save();
        this.getService(LogService.class).save();
        this.getService(SignService.class).save();
        this.getService(NPCService.class).save();
        this.getService(ConfigService.class).save();
        this.getService(ModuleService.class).shutdown();
        this.getService(Scheduler.class).scheduleDelayedTask(() -> {
            try {
                FileUtils.deleteDirectory(this.getService(FileService.class).getDynamicServerDirectory());
            } catch (IOException ignored) {}
        }, 20L);
        this.getService(Scheduler.class).scheduleDelayedTask(() -> this.getService(CloudNetworkService.class).shutdown(), 60L);
        this.getService(Scheduler.class).scheduleDelayedTask(() -> System.exit(0), 80L);
    }


}
