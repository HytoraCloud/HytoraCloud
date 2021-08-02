package de.lystx.hytoracloud.bridge.proxy.velocity.listener.player;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.proxy.velocity.VelocityBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.versions.MinecraftProtocol;
import de.lystx.hytoracloud.driver.commons.events.EventResult;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerConnectionObject;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerJoin;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;

import java.util.concurrent.TimeUnit;

public class PlayerLoginListener {


    @Subscribe
    public void handle(DisconnectEvent event) {
        Player player = event.getPlayer();

        ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUniqueId());

        if (cloudPlayer == null) {
            return;
        }
        CloudBridge.getInstance().getProxyBridge().playerQuit(cloudPlayer);
    }


    @Subscribe @SneakyThrows
    public void handle(PlayerChooseInitialServerEvent event) {
        ProxyServer velocity = VelocityBridge.getInstance().getServer();
        Player player = event.getPlayer();

        IService fallbackService = CloudDriver.getInstance().getFallbackManager().getFallback(ICloudPlayer.dummy(player.getUsername(), player.getUniqueId()));
        RegisteredServer fallbackRegisteredServer = velocity.getServer(fallbackService.getName()).orElse(null);

        if (fallbackRegisteredServer == null) {
            CloudDriver.getInstance().messageCloud(CloudDriver.getInstance().getServiceManager().getThisService().getName(), "§cVelocity-Bridge couldn't find fallback §e" + fallbackService.getName() + "§c!");
            return;
        }

        event.setInitialServer(fallbackRegisteredServer);
    }

    @Subscribe
    public void handle(com.velocitypowered.api.event.permission.PermissionsSetupEvent event) {
        PermissionSubject subject = event.getSubject();

        if (subject instanceof Player) {
            Player player = (Player)subject;

            event.setProvider(subject1 -> s -> {
                boolean b = CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), s);
                return Tristate.fromBoolean(b);
            });
        }
    }


    @Subscribe
    public void handleJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        VelocityBridge.getInstance().getServer().getScheduler().buildTask(VelocityBridge.getInstance(), () -> CloudDriver.getInstance().callEvent(new DriverEventPlayerJoin(CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUsername())))).delay(3, TimeUnit.SECONDS);
    }

    @Subscribe
    public void handleLogin(LoginEvent event) {
        Player player = event.getPlayer();

        PlayerConnectionObject connectionObject = new PlayerConnectionObject(
                player.getUniqueId(),
                player.getUsername(),
                player.getRemoteAddress().getAddress().getHostAddress(),
                player.getRemoteAddress().getPort(),
                MinecraftProtocol.valueOf(player.getProtocolVersion().getProtocol()),
                player.isOnlineMode(),
                player.getProtocolVersion().isLegacy()
        );


        EventResult login = CloudBridge.getInstance().getProxyBridge().playerLogin(connectionObject);

        if (login.isCancelled()) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text(login.getComponent())));
        } else {
            event.setResult(ResultedEvent.ComponentResult.allowed());
        }
    }
}
