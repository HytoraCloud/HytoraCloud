package de.lystx.hytoracloud.bridge.proxy.impl.listener.player;

import de.lystx.hytoracloud.bridge.proxy.events.other.ProxyServerLoginFailEvent;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerChangeServerCloudEvent;
import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.bridge.proxy.CloudProxy;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerInformation;
import io.vson.enums.FileFormat;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;


public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePreLogin(LoginEvent event) {

        PendingConnection pendingConnection = event.getConnection();

        PlayerConnection playerConnection = new PlayerConnection(
                pendingConnection.getUniqueId(),
                pendingConnection.getName(),
                pendingConnection.getAddress().getAddress().getHostAddress(),
                pendingConnection.getVersion(),
                pendingConnection.isOnlineMode(),
                pendingConnection.isLegacy()
        );

        CloudPlayer cachedPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(playerConnection.getUniqueId());

        if (cachedPlayer != null) {
            System.out.println("[CloudAPI] Logging in of [" + playerConnection.getName() + "@" + playerConnection.getUniqueId() + "] has timed out!");
            //Request timed out couldn't log in.... kicking
            event.setCancelled(true);
            ProxyServerLoginFailEvent proxyServerLoginFailEvent = new ProxyServerLoginFailEvent(event.getConnection(), ProxyServerLoginFailEvent.Reason.ALREADY_ON_NETWORK);
            ProxyServer.getInstance().getPluginManager().callEvent(proxyServerLoginFailEvent);
            if (proxyServerLoginFailEvent.isCancelled()) {
                event.setCancelReason(new TextComponent(proxyServerLoginFailEvent.getCancelReason()));
            } else {
                event.setCancelReason(new TextComponent(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getAlreadyConnectedMessage().replace("%prefix%", CloudDriver.getInstance().getCloudPrefix())));
            }
            return;
        }

        PlayerInformation playerInformation = CloudDriver.getInstance().getPermissionPool().getPlayerInformation(playerConnection.getUniqueId());

        System.out.println(playerInformation.asVsonObject().toString(FileFormat.JSON));

        cachedPlayer = new CloudPlayer(playerConnection);
        cachedPlayer.setProxy(CloudDriver.getInstance().getThisService());
        cachedPlayer.update();

        if (!CloudDriver.getInstance().getProxyConfig().isEnabled()) {
            //ProxySystem is deactivated ignoring
            return;
        }

        if (CloudDriver.getInstance().getNetworkConfig().getNetworkConfig().isMaintenance()
                && !CloudDriver.getInstance().getNetworkConfig().getNetworkConfig().getWhitelistedPlayers().contains(playerConnection.getName())
                && !CloudDriver.getInstance().getPermissionPool().hasPermission(playerConnection.getUniqueId(), "cloudsystem.network.maintenance")) {

            ProxyServerLoginFailEvent failEvent = new ProxyServerLoginFailEvent(event.getConnection(), ProxyServerLoginFailEvent.Reason.MAINTENANCE);
            ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
            if (failEvent.isCancelled()) {
                event.setCancelled(true);
                event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
            } else {
                event.setCancelled(true);
                event.setCancelReason(new TextComponent(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getMaintenanceKickMessage().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix())));
            }
        }

        if ((CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size() + 1) >= CloudDriver.getInstance().getProxyConfig().getMaxPlayers()) {
            ProxyServerLoginFailEvent failEvent = new ProxyServerLoginFailEvent(event.getConnection(), ProxyServerLoginFailEvent.Reason.NETWORK_FULL);
            ProxyServer.getInstance().getPluginManager().callEvent(failEvent);
            event.setCancelled(true);
            if (failEvent.isCancelled()) {
                event.setCancelReason(new TextComponent(failEvent.getCancelReason()));
            } else {
                //TODO: Network full message
                event.setCancelReason(new TextComponent("%prefix%&cThe network is full!".replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix())));
            }
        }
    }


    @EventHandler
    public void handlePermissionCheck(PermissionCheckEvent e) {
        UUID uniqueId = e.getSender() instanceof ProxiedPlayer ? ((ProxiedPlayer)e.getSender()).getUniqueId() : UUID.randomUUID();

        if (CloudDriver.getInstance().getPermissionPool().hasPermission(uniqueId, "*")) {
            e.setHasPermission(true);
        } else {
            e.setHasPermission(CloudDriver.getInstance().getPermissionPool().hasPermission(uniqueId, e.getPermission()));
        }
    }

    @EventHandler
    public void onCommand(ChatEvent event) {
        String message = event.getMessage();
        String player = ((ProxiedPlayer)event.getSender()).getName();

        CloudDriver.getInstance().getChannelMessenger().sendProxyChannelMessage("hytoraCloud::player", "chatMessage", new JsonEntity().append("player", player).append("message", message));
    }

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        try {
            ProxiedPlayer player = event.getPlayer();
            if (player.getServer() == null || player.getServer().getInfo() == null || player.getServer().getInfo().getName() == null) {
                ServerInfo serverInfo = CloudProxy.getInstance().getHubManager().getInfo(player);
                if (serverInfo == null) {
                    player.disconnect(CloudDriver.getInstance().getCloudPrefix() + "§cNo fallback-server was found!");
                    return;
                }
                event.setTarget(serverInfo);
            } else {
                try {
                    ServerInfo info = player.getServer().getInfo();
                    CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());
                    CloudDriver.getInstance().callEvent(new CloudPlayerChangeServerCloudEvent(cloudPlayer, info.getName()));
                } catch (NullPointerException e) {
                    // Ingoring
                }
            }
            if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
                ServiceGroup serviceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(event.getTarget().getName().split("-")[0]);
                if (serviceGroup.isMaintenance() && (!CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), "cloudsystem.group.maintenance") || !event.getPlayer().hasPermission("cloudsystem.group.maintenance"))) {
                    player.disconnect(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getGroupMaintenanceMessage().replace("&", "§").replace("%group%", serviceGroup.getName()).replace("%prefix%", CloudDriver.getInstance().getCloudPrefix()));
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
        CloudDriver.getInstance().sendPacket(new PacketUnregisterPlayer(player.getName()));

    }
}
