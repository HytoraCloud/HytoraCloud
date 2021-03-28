package de.lystx.cloudsystem.library.elements.packets.both;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.io.Serializable;

public class PacketUpdatePermissionPool extends PacketCommunication implements Serializable {


    public PacketUpdatePermissionPool(PermissionPool permissionPool) {
        this.append("enabled", permissionPool.isEnabled());
        this.append("cache", permissionPool.getPlayerCache());
        this.append("groups", permissionPool.getPermissionGroups());
    }


    public PermissionPool getPermissionPool(CloudLibrary cloudLibrary) {
        PermissionPool permissionPool = new PermissionPool(cloudLibrary);
        permissionPool.setEnabled(this.document().getBoolean("enabled"));
        permissionPool.setPermissionGroups(this.document().getList("groups", PermissionGroup.class));
        permissionPool.setPlayerCache(this.document().getList("cache", CloudPlayerData.class));
        return permissionPool;
    }
}
