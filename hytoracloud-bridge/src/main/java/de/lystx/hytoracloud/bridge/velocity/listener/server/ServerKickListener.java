package de.lystx.hytoracloud.bridge.velocity.listener.server;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

public class ServerKickListener {

    @Subscribe
    public void handle(KickedFromServerEvent event) {

        Player player = event.getPlayer();
        ICloudPlayer ICloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getUsername());
        CloudBridge.getInstance().getProxyBridge().onServerKick(ICloudPlayer, CloudDriver.getInstance().getServiceManager().getService(event.getServer().getServerInfo().getName()));

    }
}
