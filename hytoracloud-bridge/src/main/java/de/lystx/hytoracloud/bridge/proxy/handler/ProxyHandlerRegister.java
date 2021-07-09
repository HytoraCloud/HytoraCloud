package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStartedServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;



public class ProxyHandlerRegister implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {
        ProxyBridge proxyBridge = CloudBridge.getInstance().getProxyBridge();

        if (packet instanceof PacketOutRegisterServer) {
            proxyBridge.registerService(((PacketOutRegisterServer) packet).getService());
        }

        if (packet instanceof PacketOutStartedServer) {
            Service service = CloudDriver.getInstance().getServiceManager().getService(((PacketOutStartedServer) packet).getService());
            proxyBridge.registerService(service);
        }
    }
}
