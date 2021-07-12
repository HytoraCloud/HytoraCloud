package de.lystx.hytoracloud.launcher.cloud.handler.managing;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.PermissionService;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

@Getter @AllArgsConstructor
public class PacketHandlerPermissionPool implements PacketHandler {

    private final CloudSystem cloudSystem;


    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketUpdatePermissionPool) {
            PacketUpdatePermissionPool packetUpdatePermissionPool = (PacketUpdatePermissionPool)packet;
            PermissionPool permissionPool = packetUpdatePermissionPool.getPermissionPool();
            CloudDriver.getInstance().setPermissionPool(permissionPool);
            this.cloudSystem.getInstance(PermissionService.class).save();
        }
    }
}
