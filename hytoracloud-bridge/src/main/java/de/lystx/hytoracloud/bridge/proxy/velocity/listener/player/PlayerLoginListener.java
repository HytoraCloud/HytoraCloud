package de.lystx.hytoracloud.bridge.proxy.velocity.listener.player;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.events.EventResult;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
import net.kyori.adventure.text.Component;

public class PlayerLoginListener {


    @Subscribe
    public void handle(DisconnectEvent event) {
        DisconnectEvent.LoginStatus loginStatus = event.getLoginStatus();
        Player player = event.getPlayer();

        ICloudPlayer cloudPlayer = ICloudPlayer.fromUUID(player.getUniqueId());

        if (cloudPlayer == null) {
            return;
        }
        CloudBridge.getInstance().getProxyBridge().playerQuit(cloudPlayer);

    }

    @Subscribe
    public void handle(com.velocitypowered.api.event.permission.PermissionsSetupEvent event) {
        PermissionSubject subject = event.getSubject();

        if (subject instanceof Player) {
            Player player = (Player)subject;

            event.setProvider(subject1 -> {

                return (PermissionFunction) s -> {
                    boolean b = CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), s);
                    return Tristate.fromBoolean(b);
                };
            });
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


        EventResult login = CloudBridge.getInstance().getProxyBridge().playerLogin(playerConnection);

        if (login.isCancelled()) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text(login.getComponent())));
        } else {
            event.setResult(ResultedEvent.ComponentResult.allowed());
        }
    }
}
