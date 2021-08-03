package de.lystx.hytoracloud.bridge.proxy.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.proxy.ProxyBridge;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;


public class ProxyHandlerRegister implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        ProxyBridge proxyBridge = CloudBridge.getInstance().getProxyBridge();

        if (packet instanceof PacketOutRegisterServer) {
            PacketOutRegisterServer packetOutRegisterServer = (PacketOutRegisterServer)packet;
            proxyBridge.registerService(packetOutRegisterServer.getService());

        }

    }
}
