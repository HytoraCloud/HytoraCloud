package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
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
        IServiceGroup serviceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(servername.split("-")[0]);
        if (serviceGroup != null && serviceGroup.isMaintenance()) {
            if (player.hasPermission("cloudsystem.group.maintenance")) {
                event.setCancelled(false);
            } else {
                String message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getMaintenanceGroup().replace("&", "§").replace("%group%", serviceGroup.getName()).replace("%prefix%", CloudDriver.getInstance().getPrefix());

                player.sendMessage(new TextComponent(message));
                event.setCancelled(true);
            }
        }
    }

}
