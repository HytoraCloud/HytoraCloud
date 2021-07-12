package de.lystx.hytoracloud.bridge.velocity.listener.server;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.lystx.hytoracloud.bridge.velocity.VelocityBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;

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
            IService highestFallback = CloudDriver.getInstance().getFallback(cloudPlayer);
            player.createConnectionRequest(VelocityBridge.getInstance().getServer().getServer(highestFallback.getName()).orElse(null)).connect();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
