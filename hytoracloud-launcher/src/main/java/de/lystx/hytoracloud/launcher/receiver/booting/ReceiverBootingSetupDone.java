package de.lystx.hytoracloud.launcher.receiver.booting;

import de.lystx.hytoracloud.driver.service.other.Updater;
import de.lystx.hytoracloud.driver.elements.other.ReceiverInfo;
import de.lystx.hytoracloud.driver.elements.packets.receiver.PacketReceiverLogin;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
//import de.lystx.cloudsystem.receiver.handler.*;
import de.lystx.hytoracloud.launcher.receiver.handler.ReceiverPacketHandlerConfig;
import de.lystx.hytoracloud.launcher.receiver.handler.ReceiverPacketHandlerLogin;
import de.lystx.hytoracloud.launcher.receiver.handler.ReceiverPacketHandlerServer;
import de.lystx.hytoracloud.launcher.receiver.handler.ReceiverPacketHandlerShutdown;
import io.thunder.connection.base.ThunderSession;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.packet.Packet;
import io.thunder.packet.impl.PacketHandshake;
import lombok.Getter;

@Getter
public class ReceiverBootingSetupDone {

    private int tries = 0;
    private final Receiver receiver;

    public ReceiverBootingSetupDone(Receiver receiver) {
        this.receiver = receiver;
        receiver.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getParent().getConsole().getLogger().sendMessage("§b\n" +
                "    __  __      __                   ________                __\n" +
                "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                "      /____/                                                   \n" +
                "\n");
        receiver.getParent().getConsole().getLogger().sendMessage("INFO", "§9Version §7: §b" + Updater.getCloudVersion());
        receiver.getParent().getConsole().getLogger().sendMessage("INFO", "§9Developer §7: §bLystx");
        receiver.getParent().getConsole().getLogger().sendMessage("INFO", "§bLoading §fReceiver§9...");
        receiver.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§7Trying to connect to §bCloudSystem§h...");
        this.connect();
    }


    public void connect() {
        tries++;
        if (tries > 10) {
            receiver.getParent().getConsole().getLogger().sendMessage("ERROR", "§cTried §e10x §cto connect to CloudSystem! Shutting down...");
            receiver.shutdown();
            return;
        }
        ReceiverInfo info = receiver.getInstance(ConfigService.class).getReceiverInfo();
        new Thread(() -> {
            try {

                receiver.getCloudClient().addSessionListener(new ThunderListener() {

                    @Override
                    public void handleConnect(ThunderSession thunderSession) {

                        receiver.getCloudClient().addPacketHandler(new ReceiverPacketHandlerLogin(receiver));
                        receiver.getCloudClient().addPacketHandler(new ReceiverPacketHandlerShutdown(receiver));
                        receiver.getCloudClient().addPacketHandler(new ReceiverPacketHandlerConfig(receiver));
                        receiver.getCloudClient().addPacketHandler(new ReceiverPacketHandlerServer(receiver));
                        receiver.getCloudClient().sendPacket(new PacketReceiverLogin(receiver.getInstance(ConfigService.class).getReceiverInfo(), receiver.getAuthManager().getKey()));
                    }

                    @Override
                    public void handleHandshake(PacketHandshake packetHandshake) {

                    }

                    @Override
                    public void handlePacketSend(Packet packet) {

                    }

                    @Override
                    public void handlePacketReceive(Packet packet) {

                    }

                    @Override
                    public void handleDisconnect(ThunderSession thunderSession) {

                    }
                });

                receiver.getCloudClient().connect(info.getIpAddress(), info.getPort()).perform();
            } catch (Exception e) {
                receiver.getParent().getConsole().getLogger().sendMessage("ERROR", "§cNo connection to CloudSystem §ccould be build up! §h[§b" + info.getIpAddress() + "§7:" + "§b" + info.getPort() + "§7/§bTried: " + tries + "x§h]");
                this.connect();
            }
        }, "hytoraCloud_Receiver").start();
    }
}
