package de.lystx.cloudsystem.cloud.handler.services;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;


@AllArgsConstructor @Getter
public class PacketHandlerStopServer extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInStopServer) {
            try {
                PacketInStopServer packetInStopServer = (PacketInStopServer) packet;
                Service service = packetInStopServer.getService();
                if (cloudSystem.getScreenPrinter().getScreen() != null && cloudSystem.getScreenPrinter().getScreen().getScreenName().equalsIgnoreCase(service.getName())) {
                    cloudSystem.getScreenPrinter().quitCurrentScreen();
                }
                this.cloudSystem.getService().stopService(this.cloudSystem.getService().getService(service.getName()));
                this.cloudSystem.getService(Scheduler.class).scheduleDelayedTask(this.cloudSystem::reload, 2L);
            } catch (NullPointerException ignored) {}
        }
    }
}
