package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInReload;
import de.lystx.cloudsystem.library.service.packet.raw.PacketHandler;


public class PacketHandlerReload  {

    private final CloudSystem cloudSystem;

    public PacketHandlerReload(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @PacketHandler
    public void handleReload(PacketPlayInReload reload) {
        this.cloudSystem.reload();
        this.cloudSystem.reloadNPCS();
    }

}
