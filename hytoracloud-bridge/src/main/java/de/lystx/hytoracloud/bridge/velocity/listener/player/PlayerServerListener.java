package de.lystx.hytoracloud.bridge.velocity.listener.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.lystx.hytoracloud.bridge.bungeecord.HytoraCloudBungeeCordBridge;
import de.lystx.hytoracloud.bridge.velocity.HytoraCloudVelocityBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerChangeServerCloudEvent;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import net.kyori.adventure.text.Component;

import java.util.Objects;

public class PlayerServerListener {

    @Subscribe
    public void handle(ServerPreConnectEvent event) {

        try {
            Player player = event.getPlayer();
            ServerConnection serverConnection = player.getCurrentServer().orElse(null);
            if (serverConnection == null) {

                Service fallback = CloudDriver.getInstance().getFallback(CloudDriver.getInstance().getCloudPlayerManager().getPlayer(player.getUsername()));

                if (fallback == null) {
                    player.disconnect(Component.text(CloudDriver.getInstance().getCloudPrefix() + "§cNo fallback-server was found!"));
                    return;
                }
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(Objects.requireNonNull(HytoraCloudVelocityBridge.getInstance().getServer().getServer(fallback.getName()).orElse(null))));
            } else {
                try {
                    RegisteredServer info = event.getOriginalServer();

                    CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getUsername());
                    CloudDriver.getInstance().callEvent(new CloudPlayerChangeServerCloudEvent(cloudPlayer, info.getServerInfo().getName()));
                } catch (NullPointerException e) {
                    // Ingoring
                }
            }
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
        event.setResult(ServerPreConnectEvent.ServerResult.denied());
    }
}
