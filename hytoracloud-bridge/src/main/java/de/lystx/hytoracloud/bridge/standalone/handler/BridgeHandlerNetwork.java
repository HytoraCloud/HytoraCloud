package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStart;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.Event;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventListener;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


public class BridgeHandlerNetwork implements PacketHandler, EventListener {


    public BridgeHandlerNetwork() {
        CloudDriver.getInstance().getEventService().registerEvent(this);
    }

    @Event
    public void handle(DriverEventServiceStart event) {
        IService IService = event.getIService();
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerQueue(IService);
        }
    }

    public void handleRegister(PacketOutRegisterServer packet) {
        IService IService = packet.getService();
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerStart(IService);
        }
    }

    public void handleStop(PacketOutStopServer packet) {
        IService IService = CloudDriver.getInstance().getServiceManager().getService(packet.getService());
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerStop(IService);
        }
    }


    public void handleUpdate(PacketServiceUpdate packet) {
        IService IService = packet.getIService();
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            networkHandler.onServerUpdate(IService);
        }
    }

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketServiceUpdate) {
            this.handleUpdate((PacketServiceUpdate) packet);
        }
        if (packet instanceof PacketOutStopServer) {
            this.handleStop((PacketOutStopServer) packet);
        }
        if (packet instanceof PacketOutRegisterServer) {
            this.handleRegister((PacketOutRegisterServer) packet);
        }
    }
}
