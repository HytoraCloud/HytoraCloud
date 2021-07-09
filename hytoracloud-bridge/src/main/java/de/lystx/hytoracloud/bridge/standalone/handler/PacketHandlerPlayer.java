package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutPlayers;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;


import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.SneakyThrows;

import java.util.List;

public class PacketHandlerPlayer implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {

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
