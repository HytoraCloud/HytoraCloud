package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.other;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TabListener implements Listener {


    @EventHandler
    public void handle(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        CloudDriver.getInstance().getScheduler().scheduleRepeatingTask(() -> {
            ICloudPlayer cloudPlayer = ICloudPlayer.fromName(player.getName());
            CloudBridge.getInstance().getProxyBridge().updateTabList(cloudPlayer, CloudBridge.getInstance().loadRandomTablist());
        }, 0L, CloudDriver.getInstance().getProxyConfig().getTabListDelay()).cancelIf(() -> ICloudPlayer.fromUUID(player.getUniqueId()) == null);
    }


    @EventHandler
    public void handle(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        CloudDriver.getInstance().getExecutorService().execute(() -> {
            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
                CloudBridge.getInstance().getProxyBridge().updateTabList(ICloudPlayer.fromName(player.getName()), CloudBridge.getInstance().loadRandomTablist());
            }, 10L);
        });
    }
}
