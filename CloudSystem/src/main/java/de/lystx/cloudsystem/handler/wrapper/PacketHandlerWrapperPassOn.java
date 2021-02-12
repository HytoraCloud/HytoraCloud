package de.lystx.cloudsystem.handler.wrapper;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

public class PacketHandlerWrapperPassOn extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerWrapperPassOn(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (this.cloudSystem.getType().equals(CloudLibrary.Type.CLOUDSYSTEM) && cloudSystem.getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
            this.cloudSystem.getService(CloudNetworkService.class).sendPacket(packet);
        }
    }
}
