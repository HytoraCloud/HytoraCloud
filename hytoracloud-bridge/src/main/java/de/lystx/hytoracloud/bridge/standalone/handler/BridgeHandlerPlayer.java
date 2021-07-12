package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

public class BridgeHandlerPlayer implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {

        ICloudPlayerManager playerManager = CloudDriver.getInstance().getCloudPlayerManager();

        if (packet instanceof PacketUpdatePlayer) {

            PacketUpdatePlayer packetUpdatePlayer = (PacketUpdatePlayer)packet;
            ICloudPlayer ICloudPlayer = packetUpdatePlayer.getCloudPlayer();

            playerManager.update(ICloudPlayer);

        }

        if (packet instanceof PacketUnregisterPlayer) {

            PacketUnregisterPlayer packetUnregisterPlayer = (PacketUnregisterPlayer)packet;
            String name = packetUnregisterPlayer.getName();
            ICloudPlayer ICloudPlayer = playerManager.getCachedPlayer(name);

            if (ICloudPlayer != null) {
                playerManager.unregisterPlayer(ICloudPlayer);
            }
        }
    }
}
