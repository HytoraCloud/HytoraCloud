package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.server;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKickListener implements Listener {

    @EventHandler
    public void onKick(ServerKickEvent event) {
        try {
            ServerInfo serverInfo = event.getPlayer().getServer().getInfo();

            ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(event.getPlayer().getName());
            IService service = serverInfo == null ? null : CloudDriver.getInstance().getServiceManager().getCachedObject(serverInfo.getName());

            event.setCancelled(CloudBridge.getInstance().getProxyBridge().onServerKick(cloudPlayer, service));
        } catch (NullPointerException e) {
            //Ignoring on login-kick
        }
    }
}
