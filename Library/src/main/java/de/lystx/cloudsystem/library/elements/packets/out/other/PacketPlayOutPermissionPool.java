package de.lystx.cloudsystem.library.elements.packets.out.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutPermissionPool extends Packet implements Serializable {

    private final PermissionPool permissionPool;

    public PacketPlayOutPermissionPool(PermissionPool permissionPool) {
        super(PacketPlayOutPermissionPool.class);
        this.permissionPool = permissionPool;
    }
}
