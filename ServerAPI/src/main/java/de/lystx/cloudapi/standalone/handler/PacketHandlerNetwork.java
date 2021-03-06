package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerChangeServerEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerJoinEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerQuitEvent;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCallEvent;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInRegisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInUnregisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInServiceStateChange;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInServiceUpdate;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutRegisterServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStartedServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStopServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutUpdateServiceGroup;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

public class PacketHandlerNetwork extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerNetwork(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutStartedServer) {
            Service service = ((PacketPlayOutStartedServer) packet).getService();
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerQueue(service);
                networkHandler.onServerUpdate(service);
            }
        } else if (packet instanceof PacketPlayOutRegisterServer) {
            Service service = ((PacketPlayOutRegisterServer) packet).getService();
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerStart(service);
                networkHandler.onServerUpdate(service);
            }
        } else if (packet instanceof PacketPlayOutStopServer) {
            Service service = ((PacketPlayOutStopServer) packet).getService();
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerStop(service);
            }
        } else if (packet instanceof PacketPlayOutUpdateServiceGroup) {
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onGroupUpdate(((PacketPlayOutUpdateServiceGroup) packet).getServiceGroup());
                for (Service service : cloudAPI.getNetwork().getServices(cloudAPI.getNetwork().getServiceGroup(((PacketPlayOutUpdateServiceGroup) packet).getServiceGroup().getName()))) {
                    networkHandler.onServerUpdate(service);
                    networkHandler.onServerUpdate(service);
                }
            }
        } else if ( packet instanceof PacketPlayInServiceStateChange) {
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerUpdate(((PacketPlayInServiceStateChange) packet).getService());
            }
        } else if ( packet instanceof PacketPlayInServiceUpdate) {
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerUpdate(((PacketPlayInServiceUpdate) packet).getService());
            }
        }
    }


    @PacketHandler
    public void handleEvent(PacketCallEvent packet) {
        if (packet.getEvent() instanceof CloudPlayerJoinEvent) {
            CloudPlayerJoinEvent joinEvent = (CloudPlayerJoinEvent) packet.getEvent();
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onPlayerJoin(joinEvent.getCloudPlayer());
            }
            CloudAPI.getInstance().getCloudPlayers().getAll().add(joinEvent.getCloudPlayer());
        } else if (packet.getEvent() instanceof CloudPlayerChangeServerEvent) {
            CloudPlayerChangeServerEvent serverEvent = (CloudPlayerChangeServerEvent)packet.getEvent();
            CloudPlayer cloudPlayer = serverEvent.getCloudPlayer();
            cloudPlayer.setServer(serverEvent.getNewServer());

            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerChange(cloudPlayer, serverEvent.getNewServer());
            }

            CloudAPI.getInstance().getCloudPlayers().update(cloudPlayer);
        } else if (packet.getEvent() instanceof CloudPlayerQuitEvent) {
            CloudPlayerQuitEvent quitEvent = (CloudPlayerQuitEvent) packet.getEvent();
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onPlayerQuit(quitEvent.getCloudPlayer());
            }
            CloudAPI.getInstance().getCloudPlayers().getAll().remove(CloudAPI.getInstance().getCloudPlayers().get(quitEvent.getCloudPlayer().getName()));
        }
    }
}
