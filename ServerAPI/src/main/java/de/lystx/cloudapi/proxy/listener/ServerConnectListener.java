package de.lystx.cloudapi.proxy.listener;

import de.lystx.cloudapi.CloudAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerConnectListener implements Listener {


    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String servername = event.getTarget().getName();
        if (CloudAPI.getInstance().getNetwork().getServiceGroup(servername.split("-")[0]) != null && CloudAPI.getInstance().getNetwork().getServiceGroup(servername.split("-")[0]).isMaintenance())
            if (player.hasPermission("cloudsystem.group.maintenance")) {
                event.setCancelled(false);
            } else {
                String message = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getGroupMaintenanceMessage().replace("%prefix%", CloudAPI.getInstance().getPrefix());
                player.sendMessage(new TextComponent(message));
                event.setCancelled(true);
            }
    }

}
