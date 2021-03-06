package de.lystx.hytoracloud.bridge.bungeecord.listener.server;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKickListener implements Listener {

    @EventHandler
    public void onKick(ServerKickEvent event) {
        try {
            ServerInfo serverInfo = event.getPlayer().getServer().getInfo();

            ICloudPlayer cloudPlayer = ICloudPlayer.fromName(event.getPlayer().getName());
            IService service = serverInfo == null ? null : CloudDriver.getInstance().getServiceManager().getService(serverInfo.getName());

            event.setCancelled(CloudBridge.getInstance().getProxyBridge().onServerKick(cloudPlayer, service));
        } catch (NullPointerException e) {
            //IGNORING
        }
    }
}
