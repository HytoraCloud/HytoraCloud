package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutPermissionPool;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.util.List;

public class PacketHandlerPermissionPool extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerPermissionPool(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutPermissionPool) {
            //this.cloudAPI.messageCloud("CLOUDAPI", cloudAPI.getService().getName() + " received §2PermissionPool", true);
            List<CloudPlayerData> playerCache = packet.document().getList("pool", CloudPlayerData.class);
            List<PermissionGroup> permissionGroups = packet.document().getList("groups", PermissionGroup.class);
            this.cloudAPI.getPermissionPool().setPlayerCache(playerCache);
            this.cloudAPI.getPermissionPool().setPermissionGroups(permissionGroups);
        }
    }
}
