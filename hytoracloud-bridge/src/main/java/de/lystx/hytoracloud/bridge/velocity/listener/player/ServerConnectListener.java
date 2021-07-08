package de.lystx.hytoracloud.bridge.velocity.listener.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.bridge.velocity.HytoraCloudVelocityBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import net.kyori.adventure.text.Component;

public class ServerConnectListener {

    @Subscribe
    public void onConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        String servername = event.getOriginalServer().getServerInfo().getName();
        ServiceGroup serviceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(servername.split("-")[0]);
        if (serviceGroup != null && serviceGroup.isMaintenance())
            if (player.hasPermission("cloudsystem.group.maintenance")) {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(HytoraCloudVelocityBridge.getInstance().getServer().getServer(servername).get()));
            } else {
                String message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getGroupMaintenanceMessage().replace("&", "§").replace("%group%", serviceGroup.getName()).replace("%prefix%", CloudDriver.getInstance().getCloudPrefix());
                player.sendMessage(Component.text(message));
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
    }
}
