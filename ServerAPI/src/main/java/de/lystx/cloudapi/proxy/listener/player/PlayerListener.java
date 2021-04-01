package de.lystx.cloudapi.proxy.listener.player;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.other.ProxyServerLoginFailEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerChangeServerEvent;
import de.lystx.cloudsystem.library.elements.packets.both.player.PacketPlayerChat;
import de.lystx.cloudsystem.library.elements.packets.both.player.PacketRegisterPlayer;
import de.lystx.cloudsystem.library.elements.packets.both.player.PacketUnregisterPlayer;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
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
import net.md_5.bungee.event.EventPriority;


public class PlayerListener implements Listener {

    private final CloudAPI cloudAPI;

    public PlayerListener() {
        this.cloudAPI = CloudAPI.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePreLogin(LoginEvent event) {

        CloudConnection connection = new CloudConnection(event.getConnection().getUniqueId(), event.getConnection().getName(), event.getConnection().getAddress().getAddress().getHostAddress());
        CloudPlayer cloudPlayer = this.cloudAPI.getCloudPlayers().get(connection.getName());

        if (cloudPlayer == null) {
            CloudPlayer cp = new CloudPlayer(connection);
            cp.setProxy(cloudAPI.getService()); //Sets proxy of the player

            if (!CloudProxy.getInstance().getProxyConfig().isEnabled()) {
                return;
            }

            if (this.cloudAPI.getNetworkConfig().getNetworkConfig().isMaintenance()
                    &&
                    !cloudAPI.getNetworkConfig()
                            .getNetworkConfig()
                            .getWhitelistedPlayers().contains(connection.getName())
                    &&
                    !cloudAPI.getPermissionPool()
                            .hasPermission(connection.getName(), "cloudsystem.network.maintenance")) {

                ProxyServerLoginFailEvent failEvent = new ProxyServerLoginFailEvent(event.getConnection(), ProxyServerLoginFailEvent.Reason.MAINTENANCE);
                ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
                if (failEvent.isCancelled()) {
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
                } else {
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent(cloudAPI.getNetworkConfig().getMessageConfig().getMaintenanceKickMessage().replace("&", "§").replace("%prefix%", cloudAPI.getPrefix())));
                }
                return;
            }
            if ((cloudAPI.getCloudPlayers().getAll().size() + 1) >= CloudProxy.getInstance().getProxyConfig().getMaxPlayers()) {
                ProxyServerLoginFailEvent failEvent = new ProxyServerLoginFailEvent(event.getConnection(), ProxyServerLoginFailEvent.Reason.NETWORK_FULL);
                ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
                event.setCancelled(true);
                if (failEvent.isCancelled()) {
                    event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
                } else {
                    event.setCancelReason(new TextComponent("%prefix%&cThe network is full!".replace("&", "§").replace("%prefix%", cloudAPI.getPrefix())));
                }
                return;
            }
            CloudAPI.getInstance().sendPacket(new PacketRegisterPlayer(cp));
        } else {
            event.setCancelled(true);
            ProxyServerLoginFailEvent proxyServerLoginFailEvent = new ProxyServerLoginFailEvent(event.getConnection(), ProxyServerLoginFailEvent.Reason.ALREADY_ON_NETWORK);
            ProxyServer.getInstance().getPluginManager().callEvent(proxyServerLoginFailEvent);
            if (proxyServerLoginFailEvent.isCancelled()) {
                event.setCancelReason(new TextComponent(proxyServerLoginFailEvent.getCancelReason()));
            } else {
                event.setCancelReason(new TextComponent(cloudAPI.getNetworkConfig().getMessageConfig().getAlreadyOnNetworkMessage().replace("&", "§").replace("%prefix%", cloudAPI.getPrefix())));
            }
        }

    }


    @EventHandler
    public void handlePermissionCheck(PermissionCheckEvent e) {
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
        CloudAPI.getInstance().sendPacket(new PacketPlayerChat(player, message));
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
                    this.cloudAPI.callEvent(new CloudPlayerChangeServerEvent(cloudPlayer, info.getName()));
                } catch (NullPointerException e) {
                    // Ingoring
                }
            }
            if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
                ServiceGroup serviceGroup = cloudAPI.getNetwork().getServiceGroup(event.getTarget().getName().split("-")[0]);
                if (serviceGroup.isMaintenance() && (!cloudAPI.getPermissionPool().hasPermission(player.getName(), "cloudsystem.group.maintenance") || !event.getPlayer().hasPermission("cloudsystem.group.maintenance"))) {
                    player.disconnect(cloudAPI.getNetworkConfig().getMessageConfig().getGroupMaintenanceMessage().replace("&", "§").replace("%group%", serviceGroup.getName()).replace("%prefix%", cloudAPI.getPrefix()));
                    event.setCancelled(true);
                }
            }
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        CloudAPI.getInstance().sendPacket(new PacketUnregisterPlayer(player.getName()));

    }
}
