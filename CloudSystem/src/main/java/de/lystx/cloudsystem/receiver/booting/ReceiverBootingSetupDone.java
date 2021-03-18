package de.lystx.cloudsystem.receiver.booting;

import de.lystx.cloudsystem.library.service.updater.Updater;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLogin;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.receiver.Receiver;
import de.lystx.cloudsystem.receiver.handler.*;
import lombok.Getter;

@Getter
public class ReceiverBootingSetupDone {

    private int tries = 0;
    private final Receiver receiver;

    public ReceiverBootingSetupDone(Receiver receiver) {
        this.receiver = receiver;
        receiver.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getConsole().getLogger().sendMessage("§b\n" +
                "    __  __      __                   ________                __\n" +
                "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                "      /____/                                                   \n" +
                "\n");
        receiver.getConsole().getLogger().sendMessage("INFO", "§9Version §7: §b" + Updater.getCloudVersion());
        receiver.getConsole().getLogger().sendMessage("INFO", "§9Developer §7: §bLystx");
        receiver.getConsole().getLogger().sendMessage("INFO", "§bLoading §fReceiver§9...");
        receiver.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getConsole().getLogger().sendMessage("NETWORK", "§7Trying to connect to §bCloudSystem§h...");
        this.connect();
    }


    public void connect() {
        tries++;
        if (tries > 10) {
            receiver.getConsole().getLogger().sendMessage("ERROR", "§cTried §e10x §cto connect to CloudSystem! Shutting down...");
            receiver.shutdown();
            return;
        }
        ReceiverInfo info = receiver.getService(ConfigService.class).getReceiverInfo();

        new Thread(() -> {
            try {
                receiver.getCloudClient().onConnectionEstablish(nettyClient -> {
                    nettyClient.getPacketAdapter().registerAdapter(new ReceiverPacketHandlerLogin(receiver));
                    nettyClient.getPacketAdapter().registerAdapter(new ReceiverPacketHandlerShutdown(receiver));
                    nettyClient.getPacketAdapter().registerAdapter(new ReceiverPacketHandlerConfig(receiver));
                    nettyClient.getPacketAdapter().registerAdapter(new ReceiverPacketHandlerServer(receiver));
                    nettyClient.getPacketAdapter().registerAdapter(new ReceiverPacketHandlerFiles(receiver));
                    nettyClient.sendPacket(new PacketReceiverLogin(receiver.getService(ConfigService.class).getReceiverInfo(), receiver.getAuthManager().getKey()));
                });
                receiver.getCloudClient().connect(info.getIpAddress(), info.getPort());
            } catch (Exception e) {
                receiver.getConsole().getLogger().sendMessage("ERROR", "§cNo connection to CloudSystem §ccould be build up! §h[§b" + info.getIpAddress() + "§7:" + "§b" + info.getPort() + "§7/§bTried: " + tries + "x§h]");
                this.connect();
            }
        }, "hytoraCloud_Receiver").start();
    }
}
