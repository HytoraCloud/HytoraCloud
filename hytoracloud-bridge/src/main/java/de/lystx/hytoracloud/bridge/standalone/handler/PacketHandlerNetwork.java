package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStartedServer;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.commons.service.Service;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


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
        if (packet instanceof PacketOutStartedServer) {
            this.handleStart((PacketOutStartedServer) packet);
        }
    }
}
