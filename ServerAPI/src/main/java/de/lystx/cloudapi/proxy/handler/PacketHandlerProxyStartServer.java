package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutRegisterServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutStartedServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudapi.proxy.CloudProxy;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@AllArgsConstructor
public class PacketHandlerProxyStartServer extends PacketHandlerAdapter {


    private final CloudAPI cloudAPI;


    public void handle(Packet packet) {
        if (packet instanceof PacketOutRegisterServer) {
            this.register(((PacketOutRegisterServer) packet).getService());
        } else if (packet instanceof PacketOutStartedServer) {
            PacketOutStartedServer startServerPacketProxy = (PacketOutStartedServer)packet;
            this.register(startServerPacketProxy.getService());
            for (ProxiedPlayer current : ProxyServer.getInstance().getPlayers()) {
                CloudProxy.getInstance().getNetworkManager().sendStartServerMessage(current, startServerPacketProxy.getService().getName());
            }
        }
    }

    public void register(Service service) {
        ServerInfo info = ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress("127.0.0.1", service.getPort()), "CloudService", false);
        ProxyServer.getInstance().getServers().put(service.getName(), info);
    }
}
