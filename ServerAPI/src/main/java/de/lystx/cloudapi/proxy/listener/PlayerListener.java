package de.lystx.cloudapi.proxy.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.CloudLoginFailEvent;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationPlayerChat;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInCloudPlayerServerChange;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInRegisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInUnregisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.result.packets.login.ResultPacketLogin;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudapi.proxy.CloudProxy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    private final CloudAPI cloudAPI;

    public PlayerListener() {
        this.cloudAPI = CloudAPI.getInstance();
    }

    @EventHandler
    public void handlePreLogin(LoginEvent event) {
        CloudConnection connection = new CloudConnection(event.getConnection().getUniqueId(), this.cloudAPI.getPermissionPool().tryName(event.getConnection().getUniqueId()), event.getConnection().getAddress().getAddress().getHostAddress());
        CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(connection.getName());
        if (cloudPlayer == null) {
            cloudAPI.sendPacket(new PacketPlayInRegisterCloudPlayer(new CloudPlayer(connection.getName(), connection.getUuid(), connection.getAddress(), "no_server_found", cloudAPI.getNetwork().getProxy(event.getConnection().getVirtualHost().getPort()).getName())));

            if (!cloudAPI.getNetworkConfig().getProxyConfig().isEnabled()) {
                return;
            }

            if (cloudAPI.getNetworkConfig()
                    .getProxyConfig().isMaintenance()
                    &&
                    !cloudAPI.getNetworkConfig().getProxyConfig()
                            .getWhitelistedPlayers().contains(connection.getName())
                    &&
                    !cloudAPI.getPermissionPool()
                            .hasPermission(connection.getName(), "cloudsystem.network.maintenance")) {

                CloudLoginFailEvent failEvent = new CloudLoginFailEvent(event.getConnection(), CloudLoginFailEvent.Reason.MAINTENANCE);
                ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
                if (failEvent.isCancelled()) {
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
                } else {
                    event.setCancelled(true);
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
        } else {
            event.setCancelled(true);
            CloudLoginFailEvent cloudLoginFailEvent = new CloudLoginFailEvent(event.getConnection(), CloudLoginFailEvent.Reason.ALREADY_ON_NETWORK);
            ProxyServer.getInstance().getPluginManager().callEvent(cloudLoginFailEvent);
            if (cloudLoginFailEvent.isCancelled()) {
                event.setCancelReason(new TextComponent(cloudLoginFailEvent.getCancelReason()));
            } else {
                event.setCancelReason(new TextComponent(cloudAPI.getNetworkConfig().getMessageConfig().getAlreadyOnNetworkMessage().replace("&", "§").replace("%prefix%", cloudAPI.getPrefix())));
            }
        }

    }


    @EventHandler
    public void handleFail(CloudLoginFailEvent event) {
        cloudAPI.sendPacket(new PacketPlayInUnregisterCloudPlayer(event.getConnection().getName()));
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

            }
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInUnregisterCloudPlayer(player.getName()));
    }


}
