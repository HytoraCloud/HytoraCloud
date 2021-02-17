package de.lystx.cloudsystem.receiver;

import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.Updater;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutGlobalInfo;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLogin;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverShutdown;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.setup.impl.ReceiverSetup;
import de.lystx.cloudsystem.receiver.handler.ReceiverPacketHandlerLogin;
import de.lystx.cloudsystem.receiver.handler.ReceiverPacketHandlerServer;
import de.lystx.cloudsystem.receiver.handler.ReceiverPacketHandlerShutdown;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Getter @Setter
public class Receiver extends CloudInstance {

    @Getter
    private static Receiver instance;

    int tries = 0;

    public Receiver() {
        super(Type.RECEIVER);
        instance = this;
        
        
        if (this.getService(ConfigService.class).getReceiverInfo().isEstablished()) {

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
            this.console.getLogger().sendMessage("INFO", "§bLoading §fReceiver§9...");
            this.console.getLogger().sendMessage("§9-----------------------------------------");
            this.console.getLogger().sendMessage("NETWORK", "§7Trying to connect to §bCloudSystem§h...");

            this.connect();

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
            this.console.getLogger().sendMessage("KNOWN-BUG", "§7» §cSetup crashes if trying to use history (arrow keys)");
            this.console.getLogger().sendMessage("KNOWN-BUG", "§7» §cPort might have to enter multiple times (If 3 times > Kill process and restart)");
            this.console.getLogger().sendMessage("§9-----------------------------------------");
            this.getService(CommandService.class).setActive(false);
            new ReceiverSetup().start(this.console, setup -> {
                this.getService(CommandService.class).setActive(true);
                ReceiverSetup receiverSetup = (ReceiverSetup) setup;
                if (receiverSetup.isCancelled()) {
                    console.getLogger().sendMessage("ERROR", "§cYou are not allowed to cancel this setup!");
                    System.exit(0);
                    return;
                }
                this.getService(FileService.class).copyFileWithURL("/implements/versions/spigot/spigot.jar", new File(this.getService(FileService.class).getVersionsDirectory(), "spigot.jar"));
                this.getService(FileService.class).copyFileWithURL("/implements/versions/bungeecord/bungeeCord.jar", new File(this.getService(FileService.class).getVersionsDirectory(), "bungeeCord.jar"));
                this.getService(FileService.class).copyFileWithURL("/implements/server-icon.png", new File(this.getService(FileService.class).getGlobalDirectory(), "server-icon.png"));
                this.getService(FileService.class).copyFileWithURL("/implements/plugins/LabyModAPI.jar", new File(this.getService(FileService.class).getSpigotPluginsDirectory(), "LabyModAPI.jar"));
                ReceiverInfo receiverInfo = new ReceiverInfo(receiverSetup.getName(), receiverSetup.getHost(), receiverSetup.getPort(), true);
                this.getService(ConfigService.class).setReceiverInfo(receiverInfo);
                this.getService(ConfigService.class).save();
                this.console.getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The receiver will now stop and you will have to restart it...");
                System.exit(0);
            });
        }
     }


    @Override
    public void reload() {
        
    }

    @Override
    public void shutdown() {
        this.sendPacket(new PacketReceiverShutdown(this.getService(ConfigService.class).getReceiverInfo()));
        super.shutdown();
    }

    @Override
    public void sendPacket(Packet packet) {
        this.cloudClient.sendPacket(packet);
    }

    public ServiceGroup getGroup(String name) {
        return ((LinkedList<ServiceGroup>)this.customs.get("groups")).stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void connect() {
        tries++;
        if (tries > 10) {
            this.console.getLogger().sendMessage("ERROR", "§cTried §e10x §cto connect to CloudSystem! Shutting down...");
            this.shutdown();
            return;
        }
        ReceiverInfo info = this.getService(ConfigService.class).getReceiverInfo();

        new Thread(() -> {
            try {
                this.cloudClient.onConnectionEstablish(nettyClient -> {
                    nettyClient.getPacketAdapter().registerAdapter(new ReceiverPacketHandlerLogin(this));
                    nettyClient.getPacketAdapter().registerAdapter(new ReceiverPacketHandlerShutdown(this));
                    nettyClient.getPacketAdapter().registerAdapter(new ReceiverPacketHandlerServer(this));
                    nettyClient.sendPacket(new PacketReceiverLogin(this.getService(ConfigService.class).getReceiverInfo(), authManager.getKey()));
                });
                this.cloudClient.connect(info.getIpAddress(), info.getPort());
            } catch (Exception e) {
                this.console.getLogger().sendMessage("ERROR", "§cNo connection to CloudSystem §ccould be build up! §h[§b" + info.getIpAddress() + "§7:" + "§b" + info.getPort() + "§7/§bTried: " + tries + "x§h]");
                this.connect();
            }
        }, "hytoraCloud_Receiver").start();
    }
}
