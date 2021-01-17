package de.lystx.cloudsystem.library.elements.packets.out.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutPermissionPool extends Packet implements Serializable {

    public PacketPlayOutPermissionPool(PermissionPool permissionPool) {
        this.append("key", "pool");
        this.append("groups", permissionPool.getPermissionGroups());
        this.append("pool", permissionPool.getPlayerCache());
    }
}
