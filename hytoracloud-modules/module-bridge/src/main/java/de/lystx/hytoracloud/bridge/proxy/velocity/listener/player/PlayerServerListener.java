package de.lystx.hytoracloud.bridge.proxy.velocity.listener.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.proxy.velocity.VelocityBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import net.kyori.adventure.text.Component;

import java.util.Objects;

public class PlayerServerListener {


    @Subscribe
    public void handle(ServerConnectedEvent event) {

        Player player = event.getPlayer();
        RegisteredServer server = event.getServer();

        ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUniqueId());
        IService IService = CloudDriver.getInstance().getServiceManager().getCachedObject(server.getServerInfo().getName());

        CloudBridge.getInstance().getProxyBridge().onServerConnect(cloudPlayer, IService);
    }


    @Subscribe
    public void handle(ServerPreConnectEvent event) {

        try {
            Player player = event.getPlayer();
            ServerConnection serverConnection = player.getCurrentServer().orElse(null);
            if (serverConnection == null) {

                IService fallback = CloudDriver.getInstance().getFallbackManager().getFallback(CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUsername()));

                if (fallback == null) {
                    player.disconnect(Component.text(CloudDriver.getInstance().getPrefix() + "§cNo fallback-server was found!"));
                    return;
                }
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(Objects.requireNonNull(VelocityBridge.getInstance().getServer().getServer(fallback.getName()).orElse(null))));
            }
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
        event.setResult(ServerPreConnectEvent.ServerResult.denied());
    }
}
