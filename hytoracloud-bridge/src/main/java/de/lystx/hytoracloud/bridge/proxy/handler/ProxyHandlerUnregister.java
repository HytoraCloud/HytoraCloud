package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import net.md_5.bungee.api.ProxyServer;

public class ProxyHandlerUnregister implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            Service service = CloudDriver.getInstance().getServiceManager().getService(packetOutStopServer.getService());
            CloudBridge.getInstance().getProxyBridge().stopServer(service);
        }
    }
}
