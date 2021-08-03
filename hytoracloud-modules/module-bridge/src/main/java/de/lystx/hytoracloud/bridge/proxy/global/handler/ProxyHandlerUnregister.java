package de.lystx.hytoracloud.bridge.proxy.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;


public class ProxyHandlerUnregister implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            IService IService = CloudDriver.getInstance().getServiceManager().getCachedObject(packetOutStopServer.getService());
            CloudBridge.getInstance().getProxyBridge().stopServer(IService);
        }
    }
}
