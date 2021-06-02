package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionPool;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import lombok.Getter;

@Getter
public class PacketHandlerPermissionPool implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketUpdatePermissionPool) {
            PacketUpdatePermissionPool packetUpdatePermissionPool = (PacketUpdatePermissionPool)packet;
            PermissionPool permissionPool = packetUpdatePermissionPool.getPermissionPool();
            CloudDriver.getInstance().setPermissionPool(permissionPool);

        }
    }
}
