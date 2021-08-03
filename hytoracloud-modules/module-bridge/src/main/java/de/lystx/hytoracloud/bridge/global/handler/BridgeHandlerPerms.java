package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;


import lombok.Getter;

@Getter
public class BridgeHandlerPerms implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketUpdatePermissionPool) {
            PacketUpdatePermissionPool packetUpdatePermissionPool = (PacketUpdatePermissionPool)packet;
            PermissionPool permissionPool = packetUpdatePermissionPool.getPermissionPool();
            CloudDriver.getInstance().setInstance("permissionPool", permissionPool);

        }
    }
}
