package de.lystx.cloudsystem.wrapper;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.Updater;
import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketGlobalInfo;
import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketLogOut;
import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketLoginRequest;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.console.LoggerService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.key.AuthManager;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.setup.impl.WrapperSetup;
import de.lystx.cloudsystem.wrapper.handler.*;
import de.lystx.cloudsystem.wrapper.manager.ConfigManager;
import de.lystx.cloudsystem.wrapper.manager.FileManager;
import de.lystx.cloudsystem.wrapper.manager.ServerManager;
import de.lystx.cloudsystem.wrapper.utils.WrapperClient;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;


@Getter @Setter
public class Wrapper extends CloudLibrary {

    private final Scheduler scheduler;

    private final WrapperClient wrapperClient;
    private final FileManager fileManager;
    private final ConfigManager configManager;
    private final AuthManager authManager;
    private final ServerManager serverManager;

    private final LoggerService loggerService;
    private final CommandService commandService;
    private final TemplateService templateService;
    private boolean connected;

    public Wrapper() {
        super(Type.WRAPPER);
        this.connected = false;
        this.scheduler = new Scheduler(this, "Scheduler", CloudService.Type.MANAGING);
        this.fileManager = new FileManager(this);
        this.configManager = new ConfigManager(this);
        this.commandService = new CommandService(this, "Command", CloudService.Type.MANAGING);
        this.loggerService = new LoggerService(this, "Logger", CloudService.Type.MANAGING);
        this.templateService = new TemplateService(this, "Templates", CloudService.Type.MANAGING);

        this.cloudServices.add(this.templateService);

        this.console = new CloudConsole(this.loggerService, this.commandService, System.getProperty("user.name"));
        this.wrapperClient = new WrapperClient(this.configManager.getHost(), this.configManager.getPort(), this.getNetworkChannel());
        this.authManager = new AuthManager(new File("wrapper.cloudkey"));
        this.serverManager = new ServerManager(this);

        if (!this.configManager.isSetupDone()) {
            this.console.getLogger().sendMessage("§9-----------------------------------------");
            this.console.getLogger().sendMessage("§b\n" +
                    "   _____      __            \n" +
                    "  / ___/___  / /___  ______ \n" +
                    "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                    " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                    "/____/\\___/\\__/\\__,_/ .___/ \n" +
                    "                   /_/      \n");
            this.console.getLogger().sendMessage("§9-----------------------------------------");
            this.console.getLogger().sendMessage("SETUP", "§cSeems like you haven't set up this §eWrapper§c yet§c!");
            this.console.getLogger().sendMessage("SETUP", "§cLet's fix this quite quick...");
            this.console.getLogger().sendMessage("SETUP", "§cKnown bugs:");
            this.console.getLogger().sendMessage("SETUP", "  §7» §cConsole prefix shown up twice (Only in Setup)");
            this.console.getLogger().sendMessage("SETUP", "  §7» §cSetup crashes if trying to use history (arrow keys)");
            this.console.getLogger().sendMessage("SETUP", "  §7» §cPort might have to enter multiple times (If 3 times > Kill process and restart)");
            this.console.getLogger().sendMessage();
            this.console.getLogger().sendMessage();
            this.commandService.setActive(false);
            new WrapperSetup().start(this.console, setup -> {

                this.fileManager.copyFileWithURL("/implements/versions/spigot/spigot.jar", new File(this.fileManager.getVersionsDirectory(), "spigot.jar"));
                this.fileManager.copyFileWithURL("/implements/versions/bungeecord/bungeeCord.jar", new File(this.fileManager.getVersionsDirectory(), "bungeeCord.jar"));
                this.fileManager.copyFileWithURL("/implements/server-icon.png", new File(this.fileManager.getGlobalDirectory(), "server-icon.png"));
                this.fileManager.copyFileWithURL("/implements/plugins/LabyModAPI.jar", new File(this.fileManager.getSpigotPluginsDirectory(), "LabyModAPI.jar"));
                WrapperSetup wrapperSetup = (WrapperSetup)setup;
                configManager.getDocument().append("setupDone", true);
                configManager.getDocument().append("host", wrapperSetup.getHost());
                configManager.getDocument().append("port", wrapperSetup.getPort());
                configManager.getDocument().append("name", wrapperSetup.getName());
                configManager.getDocument().save();
                commandService.setActive(true);
                console.getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The wrapper will now stop and you will have to restart it...");
                System.exit(0);
            });
        } else {


            this.console.getLogger().sendMessage("§9-----------------------------------------");
            this.console.getLogger().sendMessage("§b\n" +
                    "    __  __      __                   ________                __\n" +
                    "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                    "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                    " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                    "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                    "      /____/                                                   \n" +
                    "\n");
            this.console.getLogger().sendMessage("INFO", "§9Version §7: §b" + Updater.getCloudVersion());
            this.console.getLogger().sendMessage("INFO", "§9Developer §7: §bLystx");
            this.console.getLogger().sendMessage("INFO", "§bLoading §fWrapper§9...");
            this.console.getLogger().sendMessage("§9-----------------------------------------");
            String key = this.authManager.getKey();
            if (key.equalsIgnoreCase("null")) {
                console.getLogger().sendMessage("ERROR", "§cNo §eCloudKey §cwas found to connect to CloudSystem!");
                console.getLogger().sendMessage("ERROR", "§cShutting down Wrapper!");
                System.exit(0);
                return;
            }
            this.wrapperClient.registerPacketHandler(new PacketHandlerLogin(this));
            this.wrapperClient.registerPacketHandler(new PacketHandlerShutdown(this));
            this.wrapperClient.registerPacketHandler(new PacketHandlerStartServer(this));
            this.wrapperClient.registerPacketHandler(new PacketHandlerRegisterServer(this));
            this.wrapperClient.registerPacketHandler(new PacketHandlerStopServer(this));
            this.wrapperClient.registerPacketHandler(new PacketHandlerGlobalInfo(this));
            try {
                this.wrapperClient.connect();
            } catch (IOException e) {
                console.getLogger().sendMessage("ERROR", "§cNo connection to CloudSystem could be build up!");
                console.getLogger().sendMessage("ERROR", "§cShutting down Wrapper!");
                System.exit(0);
            }
            this.wrapperClient.sendPacket(new WrapperPacketLoginRequest(this.configManager.getName(), key));
        }
        Runtime.getRuntime().addShutdownHook(this.shutdownHook());
    }



    public Thread shutdownHook() {
        return new Thread(() -> {
            this.wrapperClient.sendPacket(new WrapperPacketLogOut(this.configManager.getName()));
            this.wrapperClient.disconnect();
            this.scheduler.scheduleDelayedTask(() -> System.exit(0), 5L);
        });
    }
}
