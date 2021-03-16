package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerChangeServerEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerJoinEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerQuitEvent;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.both.PacketCallEvent;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceStateChange;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutRegisterServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutStartedServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
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
            this.cloudAPI.getCloudClient().getNetworkHandlers().forEach(networkHandler -> {
                networkHandler.onServerStart(service);
                networkHandler.onServerUpdate(service);
            });
        } else if (packet instanceof PacketOutStopServer) {
            Service service = ((PacketOutStopServer) packet).getService();
            this.cloudAPI.getCloudClient().getNetworkHandlers().forEach(networkHandler -> networkHandler.onServerStop(service));
        } else if ( packet instanceof PacketInServiceStateChange) {
            this.cloudAPI.getCloudClient().getNetworkHandlers().forEach(networkHandler -> networkHandler.onServerUpdate(((PacketInServiceStateChange) packet).getService()));
        } else if ( packet instanceof PacketInServiceUpdate) {
            this.cloudAPI.getCloudClient().getNetworkHandlers().forEach(networkHandler -> networkHandler.onServerUpdate(((PacketInServiceUpdate) packet).getService()));

        }
    }

    @PacketHandler
    public void handleEvent(PacketCallEvent packet) {
        if (packet.getEvent() instanceof CloudPlayerJoinEvent) {
            CloudPlayerJoinEvent joinEvent = (CloudPlayerJoinEvent) packet.getEvent();
            this.cloudAPI.getCloudClient().getNetworkHandlers().forEach(networkHandler -> networkHandler.onPlayerJoin(joinEvent.getCloudPlayer()));
            CloudAPI.getInstance().getCloudPlayers().getAll().add(joinEvent.getCloudPlayer());
        } else if (packet.getEvent() instanceof CloudPlayerChangeServerEvent) {
            CloudPlayerChangeServerEvent serverEvent = (CloudPlayerChangeServerEvent)packet.getEvent();
            CloudPlayer cloudPlayer = serverEvent.getCloudPlayer();
            cloudPlayer.setServer(serverEvent.getNewServer());

            this.cloudAPI.getCloudClient().getNetworkHandlers().forEach(networkHandler -> networkHandler.onServerChange(cloudPlayer, serverEvent.getNewServer()));

            CloudAPI.getInstance().getCloudPlayers().update(cloudPlayer);
        } else if (packet.getEvent() instanceof CloudPlayerQuitEvent) {
            CloudPlayerQuitEvent quitEvent = (CloudPlayerQuitEvent) packet.getEvent();
            this.cloudAPI.getCloudClient().getNetworkHandlers().forEach(networkHandler -> networkHandler.onPlayerQuit(quitEvent.getCloudPlayer()));
            CloudAPI.getInstance().getCloudPlayers().getAll().remove(CloudAPI.getInstance().getCloudPlayers().get(quitEvent.getCloudPlayer().getName()));
        }
    }
}
