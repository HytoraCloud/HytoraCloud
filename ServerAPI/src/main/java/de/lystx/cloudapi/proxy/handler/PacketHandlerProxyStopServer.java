package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStopServer;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudapi.proxy.CloudProxy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PacketHandlerProxyStopServer extends PacketHandlerAdapter {


    private final CloudAPI cloudAPI;

    public PacketHandlerProxyStopServer(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutStopServer) {
            PacketPlayOutStopServer stopServerPacketProxy = (PacketPlayOutStopServer)packet;
            ProxyServer.getInstance().getServers().remove(stopServerPacketProxy.getService().getName());
            for (ProxiedPlayer current : ProxyServer.getInstance().getPlayers()) {
                CloudProxy.getInstance().getNetworkManager().sendStopServerMessage(current, stopServerPacketProxy.getService().getName());
            }
        }
    }
}
