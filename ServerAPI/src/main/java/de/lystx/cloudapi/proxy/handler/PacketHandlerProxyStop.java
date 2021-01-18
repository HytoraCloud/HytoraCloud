package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PacketHandlerProxyStop extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerProxyStop(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutStopServer) {
            PacketPlayOutStopServer packetPlayOutStopServer = (PacketPlayOutStopServer)packet;
            Service service = packetPlayOutStopServer.getService();
            if (service.getName().equalsIgnoreCase(cloudAPI.getService().getName())) {

                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    player.disconnect(this.cloudAPI.getNetworkConfig().getMessageConfig().getServerShutdownMessage().replace("&", "§").replace("%prefix%", this.cloudAPI.getPrefix()));
                }
                this.cloudAPI.getScheduler().scheduleDelayedTask(ProxyServer.getInstance()::stop, 1L);
            }
        }
    }
}
