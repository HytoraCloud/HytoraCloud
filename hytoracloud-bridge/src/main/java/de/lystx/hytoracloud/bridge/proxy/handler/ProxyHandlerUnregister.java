package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.commons.service.Service;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


public class ProxyHandlerUnregister implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            Service service = CloudDriver.getInstance().getServiceManager().getService(packetOutStopServer.getService());
            CloudBridge.getInstance().getProxyBridge().stopServer(service);
        }
    }
}
