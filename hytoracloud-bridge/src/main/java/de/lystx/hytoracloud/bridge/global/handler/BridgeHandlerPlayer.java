package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ObjectCloudPlayerManager;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

public class BridgeHandlerPlayer implements PacketHandler {

    @Override
    public void handle(Packet packet) {

        ObjectCloudPlayerManager playerManager = CloudDriver.getInstance().getPlayerManager();

        if (packet instanceof PacketUpdatePlayer) {

            PacketUpdatePlayer packetUpdatePlayer = (PacketUpdatePlayer)packet;
            ICloudPlayer cloudPlayer = packetUpdatePlayer.getCloudPlayer();

            if (cloudPlayer == null) {
                return;
            }
            playerManager.update(cloudPlayer);

        }

        if (packet instanceof PacketUnregisterPlayer) {

            PacketUnregisterPlayer packetUnregisterPlayer = (PacketUnregisterPlayer)packet;
            String name = packetUnregisterPlayer.getName();
            ICloudPlayer cloudPlayer = playerManager.getCachedObject(name);

            if (cloudPlayer != null) {
                playerManager.unregisterPlayer(cloudPlayer);
            }
        }
    }
}
