package de.lystx.hytoracloud.bridge.proxy.velocity.listener.server;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;

public class ServerKickListener {

    @Subscribe
    public void handle(KickedFromServerEvent event) {

        Player player = event.getPlayer();
        ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUsername());
        CloudBridge.getInstance().getProxyBridge().onServerKick(cloudPlayer, CloudDriver.getInstance().getServiceManager().getCachedObject(event.getServer().getServerInfo().getName()));

    }
}
