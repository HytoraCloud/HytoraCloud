package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


public class ProxyHandlerRegister implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {
        ProxyBridge proxyBridge = CloudBridge.getInstance().getProxyBridge();

        if (packet instanceof PacketOutRegisterServer) {
            PacketOutRegisterServer packetOutRegisterServer = (PacketOutRegisterServer)packet;

            proxyBridge.registerService(packetOutRegisterServer.getService());
        }

    }
}
