package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketInReload;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketOutUpdateTabList;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;


public class PacketHandlerReload  {

    private final CloudSystem cloudSystem;

    public PacketHandlerReload(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @PacketHandler
    public void handleReload(PacketInReload reload) {
        this.cloudSystem.reload();
        this.cloudSystem.reloadNPCS();
        this.cloudSystem.sendPacket(new PacketOutUpdateTabList());
        cloudSystem.syncGroupsWithServices();
    }

}
