package de.lystx.hytoracloud.bridge.velocity.listener.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.lystx.hytoracloud.bridge.velocity.HytoraCloudVelocityBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;

public class ServerKickListener {

    @Subscribe
    public void handle(KickedFromServerEvent event) {

        Player player = event.getPlayer();
        CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getUsername());
        try {
            ServerInfo serverInfo = event.getServer().getServerInfo();
            if (serverInfo == null) {
                return;
            }
            event.setResult(() -> false);
            Service highestFallback = CloudDriver.getInstance().getFallback(cloudPlayer);
            player.createConnectionRequest(HytoraCloudVelocityBridge.getInstance().getServer().getServer(highestFallback.getName()).orElse(null)).connect();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
