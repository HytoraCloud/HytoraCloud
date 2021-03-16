package de.lystx.cloudapi.proxy.listener;

import de.lystx.cloudapi.proxy.CloudProxy;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKickListener implements Listener {

    @EventHandler
    public void onKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        try {
            ServerInfo serverInfo = event.getPlayer().getServer().getInfo();
            if (serverInfo == null) {
                return;
            }
            event.setCancelled(true);
            CloudProxy.getInstance().getHubManager().sendPlayerToFallback(player);
        } catch (NullPointerException e) {
            CloudProxy.getInstance().getHubManager().sendPlayerToFallback(player);
        }
        event.setCancelled(false);
    }
}
