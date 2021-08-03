package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.player;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.BungeeBridge;
import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.player.required.IPlayerConnection;
import de.lystx.hytoracloud.driver.utils.enums.versions.MinecraftProtocol;
import de.lystx.hytoracloud.driver.event.events.EventResult;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.event.events.player.other.DriverEventPlayerJoin;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.wrapped.PlayerConnectionObject;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.wrapped.PlayerObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePreLogin(LoginEvent event) {

        PendingConnection pendingConnection = event.getConnection();

        IPlayerConnection playerConnection = new PlayerConnectionObject(
                pendingConnection.getUniqueId(),
                pendingConnection.getName(),
                pendingConnection.getAddress().getAddress().getHostAddress(),
                pendingConnection.getAddress().getPort(),
                MinecraftProtocol.valueOf(pendingConnection.getVersion()),
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

        CloudDriver.getInstance().getMessageManager().sendChannelMessage(
                IChannelMessage.builder()
                        .channel("cloud::main")
                        .key("PLAYER_CHAT_EVENT")
                        .document(JsonObject.serializable()
                                .append("message", message)
                                .append("player", player)
                        )
                        .build()
        );

    }

    @EventHandler
    public void onConnected(ServerConnectedEvent event) {

        ProxiedPlayer player = event.getPlayer();
        ServerInfo server = event.getServer().getInfo();

        ICloudPlayer iCloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUniqueId());
        IService service = CloudDriver.getInstance().getServiceManager().getCachedObject(server.getName());

        if (iCloudPlayer == null || service == null) {
            return;
        }

        CloudBridge.getInstance().getProxyBridge().onServerConnect(iCloudPlayer, service);
    }


    @EventHandler
    public void handle(ServerConnectEvent event) {

        ProxiedPlayer player = event.getPlayer();
        Server playerServer = player.getServer();
        ServerInfo target = event.getTarget();
        ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getOptional(player.getName()).orElse(ICloudPlayer.dummy(player.getName(), player.getUniqueId()));

        if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY) || playerServer == null) {
            IService fallback = CloudDriver.getInstance().getFallbackManager().getFallback(cloudPlayer);
            ServerInfo fallbackInfo = ProxyServer.getInstance().getServerInfo(fallback.getName());

            if (fallbackInfo == null) {
                player.disconnect(CloudDriver.getInstance().getPrefix() + "§cNo fallback-server was found!");
                return;
            }
            event.setTarget(fallbackInfo);
            ((PlayerObject)cloudPlayer).setService(fallback);
            cloudPlayer.update();
        } else {
            if (target != null) {
                ((PlayerObject) cloudPlayer).setService(CloudDriver.getInstance().getServiceManager().getCachedObject(target.getName()));
                cloudPlayer.update();
            }
        }
        IServiceGroup serviceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(event.getTarget().getName().split("-")[0]);
        if (serviceGroup.isMaintenance() && (!CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), "cloudsystem.group.maintenance") || !event.getPlayer().hasPermission("cloudsystem.group.maintenance"))) {

            if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
                player.disconnect(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getMaintenanceGroup().replace("&", "§").replace("%group%", serviceGroup.getName()).replace("%prefix%", CloudDriver.getInstance().getPrefix()));
            } else {
                String message = CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getMaintenanceGroup().replace("&", "§").replace("%group%", serviceGroup.getName()).replace("%prefix%", CloudDriver.getInstance().getPrefix());
                player.sendMessage(new TextComponent(message));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ProxyServer.getInstance().getScheduler().schedule(BungeeBridge.getInstance(), () -> {
            CloudDriver.getInstance().getEventManager().callEvent(new DriverEventPlayerJoin(CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getName())));
        }, 3L, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getName());

        CloudBridge.getInstance().getProxyBridge().playerQuit(cloudPlayer);
    }
}
