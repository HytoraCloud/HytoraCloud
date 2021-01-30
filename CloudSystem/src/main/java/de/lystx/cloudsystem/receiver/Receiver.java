package de.lystx.cloudsystem.receiver;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketReceiverRegister;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.console.LoggerService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.setup.impl.ReceiverSetup;
import de.lystx.cloudsystem.receiver.elements.ReceiverClient;
import de.lystx.cloudsystem.receiver.handler.PacketHandlerReceiverRegister;
import de.lystx.cloudsystem.receiver.manager.ConfigManager;
import de.lystx.cloudsystem.receiver.manager.FileManager;
import lombok.Getter;

import java.net.Inet4Address;
import java.net.UnknownHostException;


@Getter
public class Receiver extends CloudLibrary {

    private final FileManager fileManager;
    private final ConfigManager configManager;

    private final CommandService commandService;
    private final FileService fileService;
    private final LoggerService loggerService;
    private final ReceiverClient client;

    private ServerService serverService;

    public Receiver() {
        super();
        this.commandService = new CommandService(this, "Command", CloudService.Type.MANAGING);
        this.loggerService = new LoggerService(this, "CloudLogger", CloudService.Type.UTIL);
        this.fileService = new FileService(this, "Files", CloudService.Type.MANAGING);
        this.console = new CloudConsole(this.loggerService, this.commandService, System.getProperty("user.name"));
        this.fileManager = new FileManager(this);
        this.configManager = new ConfigManager(this);
        this.client = new ReceiverClient(this.configManager.getHost(), this.configManager.getPort(), this.getNetworkChannel());

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
            this.console.getLogger().sendMessage("SETUP", "§cSeems like you haven't set up this §eReceiver§c yet§c!");
            this.console.getLogger().sendMessage("SETUP", "§cLet's fix this quite quick...");
            this.console.getLogger().sendMessage("SETUP", "§cKnown bugs:");
            this.console.getLogger().sendMessage("SETUP", "  §7» §cConsole prefix shown up twice (Only in Setup)");
            this.console.getLogger().sendMessage("SETUP", "  §7» §cSetup crashes if trying to use history (arrow keys)");
            this.console.getLogger().sendMessage("SETUP", "  §7» §cPort might have to enter multiple times (If 3 times > Kill process and restart)");
            this.console.getLogger().sendMessage();
            this.console.getLogger().sendMessage();
            this.commandService.setActive(false);
            ReceiverSetup receiverSetup = new ReceiverSetup();
            receiverSetup.start(this.console, setup -> {
                if (setup.isCancelled()) {
                    this.console.getLogger().sendMessage("ERROR", "§cYou are §enot §callowed to §4cancel §creceiver setup! Restart the Receiver!");
                    System.exit(0);
                }
                ReceiverSetup rs = (ReceiverSetup)setup;
                this.configManager.setHost(rs.getHost());
                this.configManager.setName(rs.getName());
                this.configManager.setPort(rs.getPort());
                this.configManager.setSetupDone(true);
                this.configManager.update();
                this.commandService.setActive(true);
                this.console.getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The Receiver will now stop and you will have to restart it...");
                System.exit(0);
            });
        } else {
            this.client.registerPacketHandler(new PacketHandlerReceiverRegister(this));
            this.client.connect();
            try {
                this.client.sendPacket(new PacketReceiverRegister(Inet4Address.getLocalHost().getHostAddress(), this.configManager.getName()));
            } catch (UnknownHostException e) {
                this.console.getLogger().sendMessage("ERROR", "§cCouldn't find local address!");
            }
            this.serverService = new ServerService(this, "Servers", CloudService.Type.MANAGING);
        }
    }


    public void shutdown() {
        this.client.disconnect();
    }
}
