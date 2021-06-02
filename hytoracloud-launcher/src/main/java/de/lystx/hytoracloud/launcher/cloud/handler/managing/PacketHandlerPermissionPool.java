package de.lystx.hytoracloud.launcher.cloud.handler.managing;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketUpdatePermissionPool;
import io.thunder.packet.Packet;
import de.lystx.hytoracloud.driver.service.permission.PermissionService;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketHandlerPermissionPool implements PacketHandler {

    private final CloudSystem cloudSystem;


    public void handle(Packet packet) {
        if (packet instanceof PacketUpdatePermissionPool) {
            PacketUpdatePermissionPool packetUpdatePermissionPool = (PacketUpdatePermissionPool)packet;
            PermissionPool permissionPool = packetUpdatePermissionPool.getPermissionPool();
            CloudDriver.getInstance().setPermissionPool(permissionPool);
            this.cloudSystem.getInstance(PermissionService.class).save();
        }
    }
}
