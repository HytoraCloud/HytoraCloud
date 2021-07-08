package de.lystx.hytoracloud.bridge.velocity.listener.player;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.bridge.velocity.HytoraCloudVelocityBridge;
import de.lystx.hytoracloud.bridge.velocity.elements.PlayerPermissionProvider;
import de.lystx.hytoracloud.bridge.velocity.events.VelocityProxyLoginFailEvent;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerInformation;
import io.vson.enums.FileFormat;
import net.kyori.adventure.text.Component;

import java.util.concurrent.atomic.AtomicReference;

public class PlayerLoginListener {


    @Subscribe
    public void handle(DisconnectEvent event) {
        DisconnectEvent.LoginStatus loginStatus = event.getLoginStatus();
        Player player = event.getPlayer();
        CloudDriver.getInstance().sendPacket(new PacketUnregisterPlayer(player.getUsername()));
    }

    @Subscribe
    public void handle(com.velocitypowered.api.event.permission.PermissionsSetupEvent event) {
        PermissionSubject subject = event.getSubject();

        if (subject instanceof Player) {
            event.setProvider(new PlayerPermissionProvider((Player) subject));
        }
    }


    @Subscribe
    public void handleLogin(LoginEvent event) {
        Player player = event.getPlayer();

        PlayerConnection playerConnection = new PlayerConnection(
                player.getUniqueId(),
                player.getUsername(),
                player.getRemoteAddress().getAddress().getHostAddress(),
                -1,
                true,
                true
        );

        CloudPlayer cachedPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(playerConnection.getUniqueId());

        if (cachedPlayer != null) {
            System.out.println("[CloudAPI] Logging in of [" + playerConnection.getName() + "@" + playerConnection.getUniqueId() + "] has timed out!");
            //Request timed out couldn't log in.... kicking
            AtomicReference<String> result = new AtomicReference<>();
            VelocityProxyLoginFailEvent proxyServerLoginFailEvent = new VelocityProxyLoginFailEvent(playerConnection, VelocityProxyLoginFailEvent.Reason.ALREADY_ON_NETWORK);

            HytoraCloudVelocityBridge.getInstance().getServer().getEventManager().fire(proxyServerLoginFailEvent).thenAccept(e -> {
                if (e.isCancelled()) {
                    result.set(proxyServerLoginFailEvent.getCancelReason());
                } else {
                    result.set(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getAlreadyConnectedMessage().replace("%prefix%", CloudDriver.getInstance().getCloudPrefix()));
                }
            });
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text(result.get())));
            return;
        }

        PlayerInformation playerInformation = CloudDriver.getInstance().getPermissionPool().getPlayerInformation(playerConnection.getUniqueId());

        System.out.println(playerInformation.asVsonObject().toString(FileFormat.JSON));

        cachedPlayer = new CloudPlayer(playerConnection);
        cachedPlayer.setProxy(CloudDriver.getInstance().getThisService());
        cachedPlayer.update();

        if (!CloudDriver.getInstance().getProxyConfig().isEnabled()) {
            //ProxySystem is deactivated ignoring
            return;
        }

        if (CloudDriver.getInstance().getNetworkConfig().getGlobalProxyConfig().isMaintenance()
                && !CloudDriver.getInstance().getNetworkConfig().getGlobalProxyConfig().getWhitelistedPlayers().contains(playerConnection.getName())
                && !CloudDriver.getInstance().getPermissionPool().hasPermission(playerConnection.getUniqueId(), "cloudsystem.network.maintenance")) {

            VelocityProxyLoginFailEvent proxyServerLoginFailEvent = new VelocityProxyLoginFailEvent(playerConnection, VelocityProxyLoginFailEvent.Reason.ALREADY_ON_NETWORK);

            HytoraCloudVelocityBridge.getInstance().getServer().getEventManager().fire(proxyServerLoginFailEvent).thenAccept(e -> {

                if (e.isCancelled()) {
                    event.setResult(ResultedEvent.ComponentResult.denied(Component.text(e.getCancelReason())));
                } else {
                    event.setResult(ResultedEvent.ComponentResult.denied(Component.text(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getMaintenanceKickMessage().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix()))));
                }
            });
        }

        if ((CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size() + 1) >= CloudDriver.getInstance().getProxyConfig().getMaxPlayers()) {

            VelocityProxyLoginFailEvent proxyServerLoginFailEvent = new VelocityProxyLoginFailEvent(playerConnection, VelocityProxyLoginFailEvent.Reason.ALREADY_ON_NETWORK);

            HytoraCloudVelocityBridge.getInstance().getServer().getEventManager().fire(proxyServerLoginFailEvent).thenAccept(e -> {

                if (e.isCancelled()) {
                    event.setResult(ResultedEvent.ComponentResult.denied(Component.text(e.getCancelReason())));
                } else {
                    //TODO: Network full message
                    event.setResult(ResultedEvent.ComponentResult.denied(Component.text("%prefix%&cThe network is full!".replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix()))));
                }
            });
        }
    }
}
