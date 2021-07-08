package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutPlayers;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.SneakyThrows;

import java.util.List;

public class PacketHandlerPlayer implements PacketHandler {

    @Override
    public void handle(Packet packet) {

        if (packet instanceof PacketOutPlayers) {

            System.out.println("RECEIVED");
            PacketOutPlayers packetOutPlayers = (PacketOutPlayers)packet;
            List<CloudPlayer> cloudPlayers = packetOutPlayers.getCloudPlayers();
            CloudDriver.getInstance().getCloudPlayerManager().setOnlinePlayers(cloudPlayers);
        }

        if (packet instanceof PacketUpdatePlayer) {

            System.out.println("[TRHE]");
        }
    }
}
