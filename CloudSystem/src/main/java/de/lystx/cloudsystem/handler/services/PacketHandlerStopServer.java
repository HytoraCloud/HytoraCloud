package de.lystx.cloudsystem.handler.services;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import lombok.Getter;

@Getter
public class PacketHandlerStopServer extends PacketHandlerAdapter {


    private final CloudSystem cloudSystem;

    public PacketHandlerStopServer(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInStopServer) {
            PacketPlayInStopServer packetPlayInStopServer = (PacketPlayInStopServer) packet;
            Service service = packetPlayInStopServer.getService();
            this.cloudSystem.getService(CloudPlayerService.class).clearGroup(service.getServiceGroup().getName());
            this.cloudSystem.getService().stopService(this.cloudSystem.getService().getService(service.getName()));
        }
    }
}
