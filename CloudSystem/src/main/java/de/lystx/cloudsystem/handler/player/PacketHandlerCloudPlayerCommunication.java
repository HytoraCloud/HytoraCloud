package de.lystx.cloudsystem.handler.player;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunication;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

public class PacketHandlerCloudPlayerCommunication extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerCloudPlayerCommunication(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunication) {
            this.cloudSystem.getService(CloudNetworkService.class).sendPacket(packet);
        }
    }
}
