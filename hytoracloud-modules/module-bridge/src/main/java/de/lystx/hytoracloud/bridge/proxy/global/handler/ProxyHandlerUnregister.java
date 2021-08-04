package de.lystx.hytoracloud.bridge.proxy.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.service.IService;




public class ProxyHandlerUnregister implements IPacketHandler {

    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            IService IService = CloudDriver.getInstance().getServiceManager().getCachedObject(packetOutStopServer.getService());
            CloudBridge.getInstance().getProxyBridge().stopServer(IService);
        }
    }
}
