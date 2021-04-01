package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketHandlerPermissionPool {

    private final CloudAPI cloudAPI;

    @PacketHandler
    public void handle(PacketUpdatePermissionPool packet) {
        this.cloudAPI.setPermissionPool(packet.getPermissionPool(cloudAPI.getCloudLibrary()));
    }
}
