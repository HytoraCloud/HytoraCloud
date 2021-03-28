package de.lystx.cloudsystem.cloud.handler.managing;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.both.PacketUpdatePermissionPool;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketHandlerPermissionPool extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketUpdatePermissionPool) {
            PacketUpdatePermissionPool packetUpdatePermissionPool = (PacketUpdatePermissionPool)packet;
            PermissionPool permissionPool = packetUpdatePermissionPool.getPermissionPool(cloudSystem);
            this.cloudSystem.getService(PermissionService.class).setPermissionPool(permissionPool);
            this.cloudSystem.getService(PermissionService.class).save();
        }
    }
}
