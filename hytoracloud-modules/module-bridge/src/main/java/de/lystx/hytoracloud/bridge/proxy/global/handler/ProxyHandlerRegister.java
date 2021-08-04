package de.lystx.hytoracloud.bridge.proxy.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.proxy.ProxyBridge;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.out.PacketOutRegisterServer;




public class ProxyHandlerRegister implements IPacketHandler {

    @Override
    public void handle(IPacket packet) {
        ProxyBridge proxyBridge = CloudBridge.getInstance().getProxyBridge();

        if (packet instanceof PacketOutRegisterServer) {
            PacketOutRegisterServer packetOutRegisterServer = (PacketOutRegisterServer)packet;
            proxyBridge.registerService(packetOutRegisterServer.getService());

        }

    }
}
