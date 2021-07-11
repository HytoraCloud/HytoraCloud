package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.hytoracloud.driver.service.managing.permission.impl.PermissionPool;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import lombok.Getter;

@Getter
public class PacketHandlerPermissionPool implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketUpdatePermissionPool) {
            PacketUpdatePermissionPool packetUpdatePermissionPool = (PacketUpdatePermissionPool)packet;
            PermissionPool permissionPool = packetUpdatePermissionPool.getPermissionPool();
            CloudDriver.getInstance().setPermissionPool(permissionPool);

        }
    }
}
