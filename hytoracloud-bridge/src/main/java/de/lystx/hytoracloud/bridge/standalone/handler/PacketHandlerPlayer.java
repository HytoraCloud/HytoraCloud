package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.service.managing.player.ICloudPlayerManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;

public class PacketHandlerPlayer implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {

        ICloudPlayerManager playerManager = CloudDriver.getInstance().getCloudPlayerManager();

        if (packet instanceof PacketUpdatePlayer) {

            PacketUpdatePlayer packetUpdatePlayer = (PacketUpdatePlayer)packet;
            CloudPlayer cloudPlayer = packetUpdatePlayer.getCloudPlayer();

            playerManager.update(cloudPlayer);

        }

        if (packet instanceof PacketUnregisterPlayer) {

            PacketUnregisterPlayer packetUnregisterPlayer = (PacketUnregisterPlayer)packet;
            String name = packetUnregisterPlayer.getName();
            CloudPlayer cloudPlayer = playerManager.getCachedPlayer(name);

            if (cloudPlayer != null) {
                playerManager.unregisterPlayer(cloudPlayer);
            }
        }
    }
}
