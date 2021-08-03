package de.lystx.hytoracloud.cloud.handler.managing;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.hytoracloud.driver.player.permission.PermissionService;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

@Getter @AllArgsConstructor
public class CloudHandlerPerms implements PacketHandler {

    private final CloudSystem cloudSystem;


    public void handle(Packet packet) {
        if (packet instanceof PacketUpdatePermissionPool) {
            PacketUpdatePermissionPool packetUpdatePermissionPool = (PacketUpdatePermissionPool)packet;
            PermissionPool permissionPool = packetUpdatePermissionPool.getPermissionPool();
            CloudDriver.getInstance().setInstance("permissionPool", permissionPool);
            CloudDriver.getInstance().getServiceRegistry().getInstance(PermissionService.class).save();
        }
    }
}
