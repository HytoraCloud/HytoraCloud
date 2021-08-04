package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.out.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.packets.out.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.player.ICloudPlayerManager;




import de.lystx.hytoracloud.driver.player.ICloudPlayer;

public class BridgeHandlerPlayer implements IPacketHandler {

    @Override
    public void handle(IPacket packet) {

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
            ICloudPlayer cloudPlayer = playerManager.getCachedObject(name);

            if (cloudPlayer != null) {
                playerManager.unregisterPlayer(cloudPlayer);
            }
        }
    }
}
