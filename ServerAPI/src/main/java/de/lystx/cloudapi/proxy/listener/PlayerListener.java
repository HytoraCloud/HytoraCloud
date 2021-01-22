package de.lystx.cloudapi.proxy.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.CloudLoginFailEvent;
import de.lystx.cloudapi.proxy.events.GlobalChatEvent;
import de.lystx.cloudsystem.library.elements.other.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationPlayerChat;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInCloudPlayerServerChange;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInPlayerExecuteCommand;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInRegisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInUnregisterCloudPlayer;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final CloudAPI cloudAPI;

    public PlayerListener() {
        this.cloudAPI = CloudAPI.getInstance();
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        try {
            if (this.cloudAPI.getNetwork().getServices().isEmpty() || !this.cloudAPI.isJoinable()) {
                CloudLoginFailEvent failEvent = new CloudLoginFailEvent(event.getConnection(), CloudLoginFailEvent.Reason.NO_SERVICES);
                ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
                event.setCancelled(true);
                if (failEvent.isCancelled()) {
                    event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
                } else {
                    event.setCancelReason(new TextComponent(this.cloudAPI.getNetworkConfig().getMessageConfig().getNetworkStillBootingMessage().replace("%prefix%", this.cloudAPI.getPrefix())));
                }
                return;
            }
            String name = this.cloudAPI.getPermissionPool().tryName(event.getConnection().getUniqueId());
            if (!this.cloudAPI.getNetworkConfig().getProxyConfig().isEnabled()) {
                return;
            }
            if (this.cloudAPI.getCloudPlayers().get(name) != null) {
                CloudLoginFailEvent failEvent = new CloudLoginFailEvent(event.getConnection(), CloudLoginFailEvent.Reason.ALREADY_ON_NETWORK);
                ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
                event.setCancelled(true);
                if (failEvent.isCancelled()) {
                    event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
                } else {
                    event.setCancelReason(new TextComponent(this.cloudAPI.getNetworkConfig().getMessageConfig().getAlreadyOnNetworkMessage().replace("%prefix%", this.cloudAPI.getPrefix())));
                }
            }
            boolean is = this.cloudAPI.getPermissionPool().hasPermission(name, "cloudsystem.network.maintenance");
            if (this.cloudAPI.getNetworkConfig().getProxyConfig().isMaintenance() && !this.cloudAPI.getNetworkConfig().getProxyConfig().getWhitelistedPlayers().contains(name) && !is) {

                CloudLoginFailEvent failEvent = new CloudLoginFailEvent(event.getConnection(), CloudLoginFailEvent.Reason.MAINTENANCE);
                ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
                event.setCancelled(true);
                if (failEvent.isCancelled()) {
                    event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
                } else {
                    event.setCancelReason(new TextComponent(this.cloudAPI.getNetworkConfig().getMessageConfig().getMaintenanceKickMessage().replace("%prefix%", this.cloudAPI.getPrefix())));
                }
            }

        } catch (Exception ignored) {}
    }

    @EventHandler
    public void onCommand(ChatEvent event) {
        String message = event.getMessage();
        String player = ((ProxiedPlayer)event.getSender()).getName();
        cloudAPI.sendPacket(new PacketCommunicationPlayerChat(player, message));
    }

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player.getServer() == null || player.getServer().getInfo() == null || player.getServer().getInfo().getName() == null) {
           ServerInfo serverInfo = CloudProxy.getInstance().getHubManager().getInfo(player);
           event.setTarget(serverInfo);
        } else {
            ServerInfo info = player.getServer().getInfo();
            CloudPlayer cloudPlayer = this.cloudAPI.getCloudPlayers().get(player.getName());
            this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInCloudPlayerServerChange(cloudPlayer, info.getName()));
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onServerChange(cloudPlayer, info.getName());
            }
        }
    }


    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        CloudProxy.getInstance().updatePermissions(player);
        this.cloudAPI.getScheduler().scheduleDelayedTask(() -> {
            this.registerPlayer(player);
        }, 4L);
    }


    public void registerPlayer(ProxiedPlayer player) {
        if (player.getServer() == null) {
            return;
        }
        this.registerPlayer(player.getName(), player.getUniqueId(), player.getServer().getInfo().getName(), player.getAddress().getAddress().getHostAddress(), player.getPendingConnection().getVirtualHost().getPort());
    }

    public void registerPlayer(String name, UUID uuid, String server, String hostAddress, int proxyPort) {
        if (this.cloudAPI.getCloudPlayers().get(name) != null) {
            return;
        }
        CloudPlayer cloudPlayer = new CloudPlayer(
                name,
                uuid,
                hostAddress,
                server,
                this.cloudAPI.getNetwork().getProxy(proxyPort).getName()
        );

        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInRegisterCloudPlayer(cloudPlayer));
        for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
            networkHandler.onPlayerJoin(cloudPlayer);
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        CloudPlayer cloudPlayer = this.cloudAPI.getCloudPlayers().get(player.getName());
        if (cloudPlayer != null) {
            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onPlayerQuit(cloudPlayer);
            }
        }
        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInUnregisterCloudPlayer(player.getName()));
    }


}
