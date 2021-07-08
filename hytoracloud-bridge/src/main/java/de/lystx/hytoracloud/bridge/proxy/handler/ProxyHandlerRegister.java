package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStartedServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

public class ProxyHandlerRegister implements PacketHandler {

    @Override
    public void handle(Packet packet) {
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
