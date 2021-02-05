package de.lystx.cloudapi.proxy.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.CloudLoginFailEvent;
import de.lystx.cloudapi.proxy.events.GlobalChatEvent;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.other.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationPlayerChat;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInCloudPlayerServerChange;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInPlayerExecuteCommand;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInRegisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInUnregisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerStillOnline;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.result.Result;
import de.lystx.cloudsystem.library.result.packets.*;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.util.Value;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerListener implements Listener {

    private final CloudAPI cloudAPI;

    public PlayerListener() {
        this.cloudAPI = CloudAPI.getInstance();
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleLoginEvent(LoginEvent event) {
        CloudConnection connection = new CloudConnection(event.getConnection().getUniqueId(), this.cloudAPI.getPermissionPool().tryName(event.getConnection().getUniqueId()), event.getConnection().getAddress().getAddress().getHostAddress());
        this.cloudAPI.sendQuery(new ResultPacketCloudPlayerLoginVerify(connection)).onDocumentSet(document -> {
            if (document.get("cloudPlayer") != null) {
                CloudLoginFailEvent failEvent = new CloudLoginFailEvent(event.getConnection(), CloudLoginFailEvent.Reason.ALREADY_ON_NETWORK);
                ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
                if (failEvent.isCancelled()) {
                    event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
                } else {
                    event.setCancelReason(new TextComponent(cloudAPI.getNetworkConfig().getMessageConfig().getAlreadyOnNetworkMessage().replace("&", "§").replace("%prefix%", cloudAPI.getPrefix())));
                }
                event.setCancelled(true);
                return;
            }
            if (!cloudAPI.getNetworkConfig().getProxyConfig().isEnabled()) {
                return;
            }
            if (cloudAPI.getNetworkConfig().getProxyConfig().isMaintenance() && !cloudAPI.getNetworkConfig().getProxyConfig().getWhitelistedPlayers().contains(connection.getName()) && !cloudAPI.getPermissionPool().hasPermission(connection.getName(), "cloudsystem.network.maintenance")) {
                CloudLoginFailEvent failEvent = new CloudLoginFailEvent(event.getConnection(), CloudLoginFailEvent.Reason.MAINTENANCE);
                ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
                event.setCancelled(true);
                if (failEvent.isCancelled()) {
                    event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
                } else {
                    event.setCancelReason(new TextComponent(cloudAPI.getNetworkConfig().getMessageConfig().getMaintenanceKickMessage().replace("&", "§").replace("%prefix%", cloudAPI.getPrefix())));
                }
            }
            if ((cloudAPI.getCloudPlayers().getAll().size() + 1) >= cloudAPI.getNetworkConfig().getProxyConfig().getMaxPlayers()) {
                CloudLoginFailEvent failEvent = new CloudLoginFailEvent(event.getConnection(), CloudLoginFailEvent.Reason.NETWORK_FULL);
                ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
                event.setCancelled(true);
                if (failEvent.isCancelled()) {
                    event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
                } else {
                    event.setCancelReason(new TextComponent("%prefix%&cThe network is full!".replace("&", "§").replace("%prefix%", cloudAPI.getPrefix())));
                }
            }

        });
    }


    @EventHandler
    public void handlePluginMessage(PluginMessageEvent event) {
        /*if (event.getTag().equals("MC|BSign") || event.getTag().equals("MC|BEdit")) {
            if (customPayloadFixer) {
                event.setCancelled(true);
            }
        }*/
    }

    @EventHandler
    public void handlePermissionCheck(PermissionCheckEvent e) {
        if (!cloudAPI.getPermissionPool().isEnabled()) {
            return;
        }
        if (cloudAPI.getPermissionPool().hasPermission(e.getSender().getName(), "*")) {
            e.setHasPermission(true);
        } else {
            e.setHasPermission(cloudAPI.getPermissionPool().hasPermission(e.getSender().getName(), e.getPermission()));
        }
    }

    @EventHandler
    public void onCommand(ChatEvent event) {
        String message = event.getMessage();
        String player = ((ProxiedPlayer)event.getSender()).getName();
        cloudAPI.sendPacket(new PacketCommunicationPlayerChat(player, message));
    }

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        try {
            ProxiedPlayer player = event.getPlayer();
            if (player.getServer() == null || player.getServer().getInfo() == null || player.getServer().getInfo().getName() == null) {
                ServerInfo serverInfo = CloudProxy.getInstance().getHubManager().getInfo(player);
                if (serverInfo == null) {
                    player.disconnect(CloudAPI.getInstance().getPrefix() + "§cNo fallback-server was found!");
                    return;
                }
                event.setTarget(serverInfo);
            } else {
                try {
                    ServerInfo info = player.getServer().getInfo();
                    CloudPlayer cloudPlayer = this.cloudAPI.getCloudPlayers().get(player.getName());
                    this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInCloudPlayerServerChange(cloudPlayer, info.getName()));
                    for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                        networkHandler.onServerChange(cloudPlayer, info.getName());
                    }
                } catch (NullPointerException e) {}
            }
            if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
                ServiceGroup serviceGroup = cloudAPI.getNetwork().getServiceGroup(event.getTarget().getName().split("-")[0]);
                if (serviceGroup.isMaintenance() && (!cloudAPI.getPermissionPool().hasPermission(player.getName(), "cloudsystem.group.maintenance") || !event.getPlayer().hasPermission("cloudsystem.group.maintenance"))) {
                    player.disconnect(cloudAPI.getNetworkConfig().getMessageConfig().getGroupMaintenanceMessage().replace("&", "§").replace("%group%", serviceGroup.getName()).replace("%prefix%", cloudAPI.getPrefix()));
                    event.setCancelled(true);
                    return;
                }
                try {
                    this.registerPlayer(player, event.getTarget());
                } catch (NullPointerException e) {}
            }
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
       // CloudProxy.getInstance().updatePermissions(player);
    }


    public void registerPlayer(ProxiedPlayer player, ServerInfo server) {
        String s = "no_server_found";
        if (server != null) {
            s = server.getName();
        }
        this.registerPlayer(player.getName(), player.getUniqueId(), s, player.getAddress().getAddress().getHostAddress(), player.getPendingConnection().getVirtualHost().getPort());
    }

    public void registerPlayer(String name, UUID uuid, String server, String hostAddress, int proxyPort) {
        try {
            CloudPlayer cloudPlayer = new CloudPlayer(
                    name,
                    uuid,
                    hostAddress,
                    server,
                    this.cloudAPI.getNetwork().getProxy(proxyPort).getName()
            );

            this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInRegisterCloudPlayer(cloudPlayer));

        } catch (NullPointerException e){}
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInUnregisterCloudPlayer(player.getName()));
    }


}
