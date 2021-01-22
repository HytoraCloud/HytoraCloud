package de.lystx.cloudsystem.handler.managing;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInPermissionPool;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import lombok.Getter;

@Getter
public class PacketHandlerPermissionPool extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerPermissionPool(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInPermissionPool) {
            PacketPlayInPermissionPool packetPlayInPermissionPool = (PacketPlayInPermissionPool)packet;
            PermissionPool permissionPool = packetPlayInPermissionPool.getPermissionPool();
            this.cloudSystem.getService(PermissionService.class).setPermissionPool(permissionPool);
            this.cloudSystem.getService(PermissionService.class).save();
            this.cloudSystem.getService(PermissionService.class).load();
            this.cloudSystem.getService(PermissionService.class).loadEntries();
            this.cloudSystem.reload();
        }
    }
}
