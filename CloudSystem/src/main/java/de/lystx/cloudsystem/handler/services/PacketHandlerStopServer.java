package de.lystx.cloudsystem.handler.services;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor @Getter
public class PacketHandlerStopServer extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInStopServer) {
            try {
                PacketPlayInStopServer packetPlayInStopServer = (PacketPlayInStopServer) packet;
                Service service = packetPlayInStopServer.getService();
                if (cloudSystem.getScreenPrinter().getScreen() != null && cloudSystem.getScreenPrinter().getScreen().getName().equalsIgnoreCase(service.getName())) {
                    cloudSystem.getScreenPrinter().quitCurrentScreen();
                }
                this.cloudSystem.getService().stopService(this.cloudSystem.getService().getService(service.getName()));
                this.cloudSystem.reload();
            } catch (NullPointerException ignored) {}
        }
    }
}
