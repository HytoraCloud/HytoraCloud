package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerChangeServerCloudEvent;
import de.lystx.hytoracloud.driver.elements.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketCallEvent;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStartedServer;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

public class PacketHandlerNetwork implements PacketHandler {



    public void handleStart(PacketOutStartedServer packet) {
        Service service = CloudDriver.getInstance().getServiceManager().getService(packet.getService());
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerQueue(service);
        }
    }

    public void handleRegister(PacketOutRegisterServer packet) {
        Service service = packet.getService();
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerStart(service);
        }
    }

    public void handleStop(PacketOutStopServer packet) {
        Service service = CloudDriver.getInstance().getServiceManager().getService(packet.getService());
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerStop(service);
        }
    }


    public void handleUpdate(PacketServiceUpdate packet) {
        Service service = packet.getService();
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerUpdate(service);
        }
    }

    public void handleEvent(PacketCallEvent packet) {
       if (packet.getCloudEvent() instanceof CloudPlayerChangeServerCloudEvent) {

            CloudPlayerChangeServerCloudEvent serverEvent = (CloudPlayerChangeServerCloudEvent)packet.getCloudEvent();
            for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
                networkHandler.onServerChange(serverEvent.getCloudPlayer(), serverEvent.getNewServer());
                networkHandler.onServerUpdate(CloudDriver.getInstance().getServiceManager().getService(serverEvent.getNewServer()));
            }

        }
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCallEvent) {
            this.handleEvent((PacketCallEvent) packet);
        }
        if (packet instanceof PacketServiceUpdate) {
            this.handleUpdate((PacketServiceUpdate) packet);
        }
        if (packet instanceof PacketOutStopServer) {
            this.handleStop((PacketOutStopServer) packet);
        }
        if (packet instanceof PacketOutRegisterServer) {
            this.handleRegister((PacketOutRegisterServer) packet);
        }
        if (packet instanceof PacketOutStartedServer) {
            this.handleStart((PacketOutStartedServer) packet);
        }
    }
}
