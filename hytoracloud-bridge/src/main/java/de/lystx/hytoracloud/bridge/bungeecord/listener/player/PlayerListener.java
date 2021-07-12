package de.lystx.hytoracloud.bridge.bungeecord.listener.player;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.commons.events.EventResult;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;
import net.hytora.networking.elements.component.Component;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
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


        EventResult result = CloudBridge.getInstance().getProxyBridge().playerLogin(playerConnection);

        if (result.isCancelled()) {
            event.setCancelled(result.isCancelled());
            event.setCancelReason(result.getComponent() == null ? "§cNo reason defined" : result.getComponent());
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

        Component component = new Component();

        component.put("key", "chat_event");
        component.put("player", player);
        component.put("message", message);
        component.setChannel("cloud::main");

        CloudDriver.getInstance().getConnection().sendComponent(component);

    }

    @EventHandler
    public void onConnected(ServerConnectedEvent event) {

        ProxiedPlayer player = event.getPlayer();
        Server server = event.getServer();

        CloudPlayer cloudPlayer = CloudPlayer.fromUUID(player.getUniqueId());
        IService IService = CloudDriver.getInstance().getServiceManager().getService(server.getInfo().getName());

        if (cloudPlayer == null || IService == null) {
            return;
        }

        CloudBridge.getInstance().getProxyBridge().onServerConnect(cloudPlayer, IService);
    }


    @EventHandler
    public void handle(ServerConnectEvent event) {

        ProxiedPlayer player = event.getPlayer();
        Server playerServer = player.getServer();
        CloudPlayer cloudPlayer = CloudPlayer.dummy(player.getName(), player.getUniqueId());

        if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
            IServiceGroup IServiceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(event.getTarget().getName().split("-")[0]);
            if (IServiceGroup.isMaintenance() && (!CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), "cloudsystem.group.maintenance") || !event.getPlayer().hasPermission("cloudsystem.group.maintenance"))) {
                player.disconnect(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getGroupMaintenanceMessage().replace("&", "§").replace("%group%", IServiceGroup.getName()).replace("%prefix%", CloudDriver.getInstance().getPrefix()));
                event.setCancelled(true);
            }
        }

        if (playerServer == null) {

            IService fallback = CloudDriver.getInstance().getFallback(cloudPlayer);
            ServerInfo fallbackInfo = ProxyServer.getInstance().getServerInfo(fallback.getName());

            if (fallbackInfo == null) {
                player.disconnect(CloudDriver.getInstance().getPrefix() + "§cNo fallback-server was found!");
                return;
            }
            event.setTarget(fallbackInfo);

            cloudPlayer.setService(fallback);
            cloudPlayer.update();
        } else {

            cloudPlayer.setService(CloudDriver.getInstance().getServiceManager().getService(event.getTarget().getName()));
            cloudPlayer.update();
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        CloudPlayer cloudPlayer = CloudPlayer.fromName(player.getName());

        CloudBridge.getInstance().getProxyBridge().playerQuit(cloudPlayer);


    }
}
