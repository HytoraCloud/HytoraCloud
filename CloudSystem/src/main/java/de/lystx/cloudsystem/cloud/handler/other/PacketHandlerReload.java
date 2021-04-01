package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketInReload;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketOutUpdateTabList;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.permission.PermissionService;


public class PacketHandlerReload  {

    private final CloudSystem cloudSystem;

    public PacketHandlerReload(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @PacketHandler
    public void handleReload(PacketInReload reload) {
        this.cloudSystem.reload();
        this.cloudSystem.sendPacket(new PacketOutUpdateTabList());
        this.cloudSystem.getService(PermissionService.class).getPermissionPool().update();
        this.cloudSystem.syncGroupsWithServices();
    }

}
