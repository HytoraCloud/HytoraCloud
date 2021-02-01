package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.elements.other.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationFallback;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationKick;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSendMessage;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSendToServer;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInCloudPlayerOnline;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInRegisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerJoin;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerQuit;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerStillOnline;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutForceRegisterPlayer;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
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
            PacketCommunicationSendToServer server = (PacketCommunicationSendToServer) packet;
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(server.getName());
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server.getServer());
            if (serverInfo == null) {
                return;
            }
            player.connect(serverInfo);
        } else if (packet instanceof PacketPlayOutCloudPlayerStillOnline) {
            PacketPlayOutCloudPlayerStillOnline packetPlayOutCloudPlayerStillOnline = (PacketPlayOutCloudPlayerStillOnline) packet;
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packetPlayOutCloudPlayerStillOnline.getPlayerName());
            cloudAPI.sendPacket(new PacketPlayInCloudPlayerOnline(packetPlayOutCloudPlayerStillOnline.getPlayerName(), (player != null)));
        } else if (packet instanceof PacketPlayOutForceRegisterPlayer) {
            PacketPlayOutForceRegisterPlayer player = (PacketPlayOutForceRegisterPlayer) packet;
            ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(player.getUuid());
            if (this.cloudAPI.getCloudPlayers().get(player.getUuid()) != null) {
                return;
            }
            CloudPlayer cloudPlayer = new CloudPlayer(
                    pp.getName(),
                    pp.getUniqueId(),
                    pp.getAddress().getAddress().getHostAddress(),
                    pp.getServer().getInfo().getName(),
                    this.cloudAPI.getNetwork().getProxy(pp.getPendingConnection().getVirtualHost().getPort()).getName()
            );

            this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInRegisterCloudPlayer(cloudPlayer));
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onPlayerJoin(cloudPlayer);
            }
        } else if (packet instanceof PacketPlayOutCloudPlayerJoin) {
            PacketPlayOutCloudPlayerJoin packetPlayOutCloudPlayerJoin = (PacketPlayOutCloudPlayerJoin)packet;
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onPlayerJoin(packetPlayOutCloudPlayerJoin.getCloudPlayer());
            }
        } else if (packet instanceof PacketPlayOutCloudPlayerQuit) {
            PacketPlayOutCloudPlayerQuit packetPlayOutCloudPlayerJoin = (PacketPlayOutCloudPlayerQuit)packet;
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onPlayerQuit(packetPlayOutCloudPlayerJoin.getCloudPlayer());
            }
        } else if (packet instanceof PacketCommunicationKick) {
            PacketCommunicationKick kick = (PacketCommunicationKick)packet;
            ProxyServer.getInstance().getPlayer(kick.getName()).disconnect(kick.getReason());
        }
    }
}
