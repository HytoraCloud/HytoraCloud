package de.lystx.hytoracloud.bridge.proxy.velocity.listener.server;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.bridge.proxy.velocity.VelocityBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import net.kyori.adventure.text.Component;

public class ServerConnectListener {

    @Subscribe
    public void onConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        String servername = event.getOriginalServer().getServerInfo().getName();
        IServiceGroup IServiceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(servername.split("-")[0]);
        if (IServiceGroup != null && IServiceGroup.isMaintenance())
            if (player.hasPermission("cloudsystem.group.maintenance")) {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(VelocityBridge.getInstance().getServer().getServer(servername).get()));
            } else {
                String message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getMaintenanceGroup().replace("&", "§").replace("%group%", IServiceGroup.getName()).replace("%prefix%", CloudDriver.getInstance().getPrefix());
                player.sendMessage(Component.text(message));
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
    }
}
