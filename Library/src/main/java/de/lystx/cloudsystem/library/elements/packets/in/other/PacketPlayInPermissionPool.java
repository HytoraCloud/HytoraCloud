package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInPermissionPool extends Packet implements Serializable {

    private final PermissionPool permissionPool;

    public PacketPlayInPermissionPool(PermissionPool permissionPool) {
        super();
        this.permissionPool = permissionPool;
    }
}
