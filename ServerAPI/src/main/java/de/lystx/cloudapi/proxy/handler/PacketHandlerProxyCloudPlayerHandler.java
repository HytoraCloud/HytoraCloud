package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationFallback;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationKick;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSendMessage;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSendToServer;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInRegisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutForceRegisterPlayer;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@Getter
public class PacketHandlerProxyCloudPlayerHandler extends PacketHandlerAdapter {


    private final CloudAPI cloudAPI;

    public PacketHandlerProxyCloudPlayerHandler(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunicationSendMessage) {
            PacketCommunicationSendMessage packetPlayOutSendMessage = (PacketCommunicationSendMessage)packet;
            ProxyServer.getInstance().getPlayer(packetPlayOutSendMessage.getUuid()).sendMessage(packetPlayOutSendMessage.getMessage());
        } else if (packet instanceof PacketCommunicationFallback) {
            PacketCommunicationFallback fallback = (PacketCommunicationFallback)packet;
            CloudProxy.getInstance().getHubManager().sendPlayerToFallback(ProxyServer.getInstance().getPlayer(fallback.getName()));
        } else if (packet instanceof PacketCommunicationSendToServer) {
            PacketCommunicationSendToServer server = (PacketCommunicationSendToServer)packet;
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(server.getName());
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server.getServer());
            if (serverInfo == null) {
                return;
            }
            player.connect(serverInfo);
        } else if (packet instanceof PacketCommunicationKick) {
            PacketCommunicationKick kick = (PacketCommunicationKick)packet;
            ProxyServer.getInstance().getPlayer(kick.getName()).disconnect(kick.getReason());
        } else if (packet instanceof PacketPlayOutForceRegisterPlayer) {
            try {
                UUID uuid = ((PacketPlayOutForceRegisterPlayer) packet).getUuid();
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                CloudProxy.getInstance().updatePermissions(player);
                this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInRegisterCloudPlayer(player.getName(), player.getAddress().getAddress().getHostAddress(),
                        player.getServer().getInfo().getName(),
                        CloudAPI.getInstance().getNetwork().getProxy(player.getPendingConnection().getVirtualHost().getPort()).getName(),
                        player.getUniqueId()));
            } catch (NullPointerException e) {
                CloudAPI.getInstance().messageCloud(CloudAPI.getInstance().getService().getName(), "§cCouldn't §eforce cache §cplayer for uuid §e" + ((PacketPlayOutForceRegisterPlayer) packet).getUuid());
            }
        }
    }
}
