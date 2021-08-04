package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionPool;




import lombok.Getter;

@Getter
public class BridgeHandlerPerms implements IPacketHandler {

    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketUpdatePermissionPool) {
            PacketUpdatePermissionPool packetUpdatePermissionPool = (PacketUpdatePermissionPool)packet;
            PermissionPool permissionPool = packetUpdatePermissionPool.getPermissionPool();
            CloudDriver.getInstance().setInstance("permissionPool", permissionPool);

        }
    }
}
