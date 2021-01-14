package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutRegisterServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStartedServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudapi.proxy.CloudProxy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

public class PacketHandlerProxyStartServer extends PacketHandlerAdapter {


    private final CloudAPI cloudAPI;

    public PacketHandlerProxyStartServer(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutRegisterServer) {
            this.register(((PacketPlayOutRegisterServer) packet).getService());
        } else if (packet instanceof PacketPlayOutStartedServer) {
            PacketPlayOutStartedServer startServerPacketProxy = (PacketPlayOutStartedServer)packet;
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
