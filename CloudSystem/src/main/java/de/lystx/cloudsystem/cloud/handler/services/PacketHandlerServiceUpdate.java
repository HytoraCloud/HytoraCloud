package de.lystx.cloudsystem.cloud.handler.services;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceStateChange;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.enums.ServiceState;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

public class PacketHandlerServiceUpdate extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerServiceUpdate(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInServiceStateChange) {
            PacketInServiceStateChange serviceStateChange = (PacketInServiceStateChange)packet;
            Service service = serviceStateChange.getService();
            Service online = this.cloudSystem.getService().getService(service.getName());
            ServiceState serviceState = serviceStateChange.getServiceState();
            this.cloudSystem.getService().updateService(online, serviceState);
            this.cloudSystem.getService(CloudNetworkService.class).sendPacket(packet);
            this.cloudSystem.reload();
        }
    }
}
