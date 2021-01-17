package de.lystx.cloudsystem;

import de.lystx.cloudsystem.commands.*;
import de.lystx.cloudsystem.handler.group.PacketHandlerCoypTemplate;
import de.lystx.cloudsystem.handler.group.PacketHandlerGroupUpdate;
import de.lystx.cloudsystem.handler.managing.PacketHandlerCloudSign;
import de.lystx.cloudsystem.handler.managing.PacketHandlerConfig;
import de.lystx.cloudsystem.handler.managing.PacketHandlerMessage;
import de.lystx.cloudsystem.handler.managing.PacketHandlerPermissionPool;
import de.lystx.cloudsystem.handler.managing.PacketHandlerNPC;
import de.lystx.cloudsystem.handler.other.PacketHandlerReload;
import de.lystx.cloudsystem.handler.other.PacketHandlerShutdown;
import de.lystx.cloudsystem.handler.other.PacketHandlerSubChannel;
import de.lystx.cloudsystem.handler.player.PacketHandlerCloudPlayer;
import de.lystx.cloudsystem.handler.player.PacketHandlerCloudPlayerCommunication;
import de.lystx.cloudsystem.handler.services.PacketHandlerRegister;
import de.lystx.cloudsystem.handler.services.PacketHandlerServiceUpdate;
import de.lystx.cloudsystem.handler.services.PacketHandlerStart;
import de.lystx.cloudsystem.handler.services.PacketHandlerStopServer;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.other.*;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayers;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutServices;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCService;
import de.lystx.cloudsystem.library.service.serverselector.sign.SignService;
import de.lystx.cloudsystem.library.service.util.LogService;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.console.LoggerService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.setup.impl.CloudSetup;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Getter
public class CloudSystem extends CloudLibrary {

    @Getter
    private static CloudSystem instance;

    private final String version;
    private final CloudScreenPrinter screenPrinter;
    private ServerService service;

    public CloudSystem() {
        super();
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

        if (this.getService(ConfigService.class).getNetworkConfig().isSetupDone()) {
            this.console.getLogger().sendMessage("§9-----------------------------------------");
            this.console.getLogger().sendMessage("§b\n" +
                    "    __  __      __                   ________                __\n" +
                    "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                    "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                    " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                    "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                    "      /____/                                                   \n" +
                    "\n");
            this.console.getLogger().sendMessage("INFO", "§9Version §7: §b" + this.version);
            this.console.getLogger().sendMessage("INFO", "§9Developer §7: §bLystx");
            this.console.getLogger().sendMessage("INFO", "§bLoading §fCloudSystem§9...");
            this.console.getLogger().sendMessage("§9-----------------------------------------");

            this.cloudServices.add(new CloudNetworkService(this, "CloudNetwork", CloudService.Type.NETWORK));
            this.cloudServices.add(new ModuleService(this, "Modules", CloudService.Type.MANAGING));
            this.cloudServices.add(this.service = new ServerService(this, "Services", CloudService.Type.NETWORK));

            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerRegister(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerStopServer(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudPlayer(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerStart(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerReload(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerShutdown(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerConfig(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerGroupUpdate(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCoypTemplate(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerPermissionPool(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerMessage(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerServiceUpdate(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudSign(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudPlayerCommunication(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerNPC(this));
            this.getService(CloudNetworkService.class).registerHandler(new PacketHandlerSubChannel(this));

            this.getService(StatisticsService.class).getStatistics().add("bootedUp");
            this.reload("statistics");

        } else {
            this.console.getLogger().sendMessage("§9-----------------------------------------");
            this.console.getLogger().sendMessage("§b\n" +
                    "   _____      __            \n" +
                    "  / ___/___  / /___  ______ \n" +
                    "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                    " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                    "/____/\\___/\\__/\\__,_/ .___/ \n" +
                    "                   /_/      \n");
            this.console.getLogger().sendMessage("§9-----------------------------------------");
            this.console.getLogger().sendMessage("SETUP", "§cSeems like you haven't set up §eHytoraCloud§c yet§c!");
            this.console.getLogger().sendMessage("SETUP", "§cLet's fix this quite quick...");
            this.console.getLogger().sendMessage("SETUP", "§cKnown bugs:");
            this.console.getLogger().sendMessage("SETUP", "  §7» §cConsole prefix shown up twice (Only in Setup)");
            this.console.getLogger().sendMessage("SETUP", "  §7» §cPort might have to enter multiple times (If 3 times > Kill process and restart)");
            this.console.getLogger().sendMessage();
            this.console.getLogger().sendMessage();
            this.getService(CommandService.class).setActive(false);
            CloudSetup cloudSetup = new CloudSetup();
            cloudSetup.start(this.console, setup -> {
                if (setup.wasCancelled()) {
                    console.getLogger().sendMessage("ERROR", "§cYou are §enot §callowed to §4cancel §cthis setup! Restart the cloud!");
                    System.exit(0);
                }
                this.getService(CommandService.class).setActive(true);
                CloudSetup sp = (CloudSetup) setup;
                Document document = this.getService(ConfigService.class).getDocument();
                document.append("setupDone", true);
                document.append("fastStartup", sp.isFastStartup());
                document.append("host", sp.getHostname());
                document.append("port", sp.getPort());
                Document proxy = document.getDocument("proxyConfig");
                proxy.append("maxPlayers", sp.getMaxPlayers());
                proxy.append("whitelistedPlayers", Collections.singleton(sp.getFirstAdmin()));
                document.append("proxyConfig", proxy);
                document.save();
                PermissionPool permissionPool = this.getService(PermissionService.class).getPermissionPool();
                permissionPool.updatePermissionGroup(sp.getFirstAdmin(), permissionPool.getPermissionGroupFromName("Admin"), -1);
                permissionPool.save(this.getService(FileService.class).getPermissionsFile(), this.getService(FileService.class).getCloudPlayerDirectory());

                this.getService(GroupService.class).createGroup(new ServiceGroup(
                        UUID.randomUUID(),
                        "Bungee",
                        "default",
                        ServiceType.PROXY,
                        1,
                        1,
                        512,
                        128,
                        50,
                        100,
                        false,
                        false,
                        true
                ));


                this.getService(GroupService.class).createGroup(new ServiceGroup(
                        UUID.randomUUID(),
                        "Lobby",
                        "default",
                        ServiceType.SPIGOT,
                        2,
                        1,
                        512,
                        128,
                        50,
                        100,
                        false,
                        true,
                        true
                ));

                this.console.getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The cloud will now stop and you will have to restart it...");
                System.exit(0);
            });
        }
    }

    public void reload() {
        this.reload("all");
    }

    public void reload(String type) {
        try {
            if (type.equalsIgnoreCase("all")) {
                this.reload("config");
                this.reload("services");
                this.reload("cloudPlayers");
                this.reload("permissions");
                this.getService(Scheduler.class).scheduleDelayedTask(() -> {
                    this.reload("statistics");
                    this.reload("signs");
                    this.reload("npcs");
                }, 5L);
            } else if (type.equalsIgnoreCase("config")) {
                this.getService(ConfigService.class).reload();
                this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutNetworkConfig(this.getService(ConfigService.class).getNetworkConfig()));
            } else if (type.equalsIgnoreCase("permissions")) {
                this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutPermissionPool(this.getService(PermissionService.class).loadEntries()));
            } else if (type.equalsIgnoreCase("statistics")) {
                this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutStatistics(this.getService(StatisticsService.class).getStatistics()));
            } else if (type.equalsIgnoreCase("signs")) {
                this.getService(SignService.class).load();
                this.getService(SignService.class).loadSigns();
                this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutCloudSigns(this.getService(SignService.class).getCloudSigns(), this.getService(SignService.class).getSignLayOut().getDocument().toString()));
            } else if (type.equalsIgnoreCase("npcs")) {
                this.getService(NPCService.class).load();
                this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutNPCs(this.getService(NPCService.class).getDocument().toString()));
            } else if (type.equalsIgnoreCase("cloudPlayers")) {
               this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutCloudPlayers(this.getService(CloudPlayerService.class).getOnlinePlayers()));
            } else if (type.equalsIgnoreCase("services")) {
                this.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutServices(this.getService().getServices()));
            }
            this.getService(StatisticsService.class).getStatistics().add("reloadedCloud");
        } catch (NullPointerException e) {
            this.console.getLogger().sendMessage("ERROR", "§cYou can't reload at the moment! Try again in a few seconds!");
        }
    }

    public void shutdown() {
        this.getService(CommandService.class).setActive(false);
        this.getConsole().interrupt();
        this.setRunning(false);
        this.getService(StatisticsService.class).save();
        this.getService().stopServices();
        this.getService(LogService.class).save();
        this.getService(PermissionService.class).save();
        this.getService(SignService.class).save();
        this.getService(NPCService.class).save();
        this.getService(ConfigService.class).save();
        this.getService(ModuleService.class).shutdown();
        this.getService(Scheduler.class).scheduleDelayedTask(() -> {
            try {
                FileUtils.deleteDirectory(this.getService(FileService.class).getDynamicServerDirectory());
            } catch (IOException e) {
                try {
                    FileUtils.deleteDirectory(this.getService(FileService.class).getDynamicServerDirectory());
                } catch (IOException ignored) {}
            }
        }, 20L);
        this.getService(Scheduler.class).scheduleDelayedTask(() -> this.getService(CloudNetworkService.class).shutdown(), 60L);
        this.getService(Scheduler.class).scheduleDelayedTask(() -> System.exit(0), 80L);
    }


}
