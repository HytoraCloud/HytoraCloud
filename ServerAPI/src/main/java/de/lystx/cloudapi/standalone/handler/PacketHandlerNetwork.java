package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerChangeServerEvent;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCallEvent;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceStateChange;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutRegisterServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutStartedServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerNetwork extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketOutStartedServer) {
            Service service = ((PacketOutStartedServer) packet).getService();
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerQueue(service);
                networkHandler.onServerUpdate(service);
            }
        } else if (packet instanceof PacketOutRegisterServer) {
            Service service = ((PacketOutRegisterServer) packet).getService();
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerStart(service);
                networkHandler.onServerUpdate(service);
            }
        } else if (packet instanceof PacketOutStopServer) {
            Service service = ((PacketOutStopServer) packet).getService();
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerStop(service);
            }
        } else if ( packet instanceof PacketInServiceStateChange) {
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerUpdate(((PacketInServiceStateChange) packet).getService());
            }
        } else if ( packet instanceof PacketInServiceUpdate) {
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerUpdate(((PacketInServiceUpdate) packet).getService());
            }

        }
    }

    @PacketHandler
    public void handleEvent(PacketCallEvent packet) {
       if (packet.getEvent() instanceof CloudPlayerChangeServerEvent) {

            CloudPlayerChangeServerEvent serverEvent = (CloudPlayerChangeServerEvent)packet.getEvent();
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerChange(serverEvent.getCloudPlayer(), serverEvent.getNewServer());
            }

        }
    }
}
