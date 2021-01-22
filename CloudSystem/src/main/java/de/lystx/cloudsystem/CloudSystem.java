package de.lystx.cloudsystem;

import de.lystx.cloudsystem.booting.impl.CloudBootingSetupDone;
import de.lystx.cloudsystem.booting.impl.CloudBootingSetupNotDone;
import de.lystx.cloudsystem.commands.*;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutGlobalInfo;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
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
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter @Setter
public class CloudSystem extends CloudLibrary {

    @Getter
    private static CloudSystem instance;

    private final String version;
    private final CloudScreenPrinter screenPrinter;
    public ServerService service;

    public CloudSystem() {
        super();
        Logger.getLogger("LOGGER-CLASS").setLevel(Level.OFF);
        instance = this;
        this.version = "1.0";
        this.cloudServices.add(new CommandService(this, "Command", CloudService.Type.MANAGING));
        this.cloudServices.add(new LoggerService(this, "CloudLogger", CloudService.Type.UTIL));
        this.console = new CloudConsole(this.getService(LoggerService.class), this.getService(CommandService.class), System.getProperty("user.name"));
        this.screenPrinter = new CloudScreenPrinter(this.console, this);

        this.cloudServices.add(new FileService(this, "File", CloudService.Type.CONFIG));
        this.cloudServices.add(new ConfigService(this, "Config", CloudService.Type.CONFIG));

        this.cloudServices.add(new LogService(this, "Logging", CloudService.Type.UTIL));
        this.cloudServices.add(new StatisticsService(this, "Stats", CloudService.Type.UTIL));

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
        this.getService(CommandService.class).registerCommand(new DownloadCommand("download", "Manages spigot versions", "spigot", "bukkit", "install"));

        if (this.getService(ConfigService.class).getNetworkConfig().isSetupDone()) {
            new CloudBootingSetupDone(this);
        } else {
            new CloudBootingSetupNotDone(this);
        }
    }

    public void reload() {
        try {
            this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutGlobalInfo(
                    this.getService(ConfigService.class).getNetworkConfig(),
                    this.getService().getServices(),
                    this.getService(PermissionService.class).getPermissionPool(),
                    this.getService(CloudPlayerService.class).getOnlinePlayers(),
                    this.getService(StatisticsService.class).getStatistics(),
                    this.getService(SignService.class).getCloudSigns(),
                    this.getService(SignService.class).getSignLayOut().getDocument().toString(),
                    this.getService(NPCService.class).getDocument().toString()
            ));
        } catch (NullPointerException e) {
            this.console.getLogger().sendMessage("ERROR", "§cYou can't reload at the moment! Try again in a few seconds!");
        }
    }

    public void shutdown() {
        this.setRunning(false);
        this.getConsole().interrupt();
        this.getService().stopServices();
        this.getService(StatisticsService.class).save();
        this.getService(LogService.class).save();
        this.getService(PermissionService.class).save();
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
