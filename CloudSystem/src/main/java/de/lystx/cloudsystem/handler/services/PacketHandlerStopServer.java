package de.lystx.cloudsystem.handler.services;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PacketHandlerStopServer extends PacketHandlerAdapter {


    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInStopServer) {
            try {
                PacketPlayInStopServer packetPlayInStopServer = (PacketPlayInStopServer) packet;
                Service service = packetPlayInStopServer.getService();
                this.cloudSystem.getService(CloudPlayerService.class).clearGroup(service.getServiceGroup().getName());
                this.cloudSystem.getService().stopService(this.cloudSystem.getService().getService(service.getName()));
            } catch (NullPointerException ignored) {}
        }
    }
}
