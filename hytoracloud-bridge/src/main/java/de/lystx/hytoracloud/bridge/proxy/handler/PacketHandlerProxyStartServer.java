package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStartedServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

public class PacketHandlerProxyStartServer implements PacketHandler {


    public void register(Service service) {
        ServerInfo info = ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress(CloudDriver.getInstance().getHost().getAddress().getHostName(), service.getPort()), "CloudService", false);
        ProxyServer.getInstance().getServers().put(service.getName(), info);
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketOutRegisterServer) {
            this.register(((PacketOutRegisterServer) packet).getService());
        }
        if (packet instanceof PacketOutStartedServer) {
            this.register(CloudDriver.getInstance().getServiceManager().getService(((PacketOutStartedServer) packet).getService()));
        }
    }
}
