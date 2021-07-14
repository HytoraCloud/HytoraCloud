package de.lystx.hytoracloud.bridge.global.handler;

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

        ICloudPlayerManager playerManager = CloudDriver.getInstance().getPlayerManager();

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
            ICloudPlayer ICloudPlayer = playerManager.getCachedObject(name);

            if (ICloudPlayer != null) {
                playerManager.unregisterPlayer(ICloudPlayer);
            }
        }
    }
}
