package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutPermissionPool;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;

public class PacketHandlerPermissionPool extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerPermissionPool(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutPermissionPool) {
            this.cloudAPI.messageCloud("CLOUDAPI", cloudAPI.getService().getName() +  " received §aPermissionPool", false);
            System.out.println("[CLOUDAPI] Received PermissionPool!");
            PacketPlayOutPermissionPool packetPlayOutPermissionPool = (PacketPlayOutPermissionPool)packet;
            PermissionPool permissionPool = packetPlayOutPermissionPool.getPermissionPool();
            this.cloudAPI.setPermissionPool(permissionPool);
        }
    }
}
