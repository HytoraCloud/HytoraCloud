package de.lystx.hytoracloud.bridge.proxy.velocity.listener.other;


import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.utils.enums.versions.MinecraftProtocol;
import de.lystx.hytoracloud.driver.event.events.network.DriverEventNetworkPing;
import de.lystx.hytoracloud.driver.utils.interfaces.PlaceHolder;
import de.lystx.hytoracloud.driver.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.wrapped.PlayerConnectionObject;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@Getter
public class ProxyPingListener {


    private int nullPointers;

    public ProxyPingListener() {
        this.nullPointers = 0;
    }


    @Subscribe
    public void handle(ProxyPingEvent event) {
        try {
            int port = event.getConnection().getVirtualHost().orElse(null).getPort();
            ServerPing ping = event.getPing();

            ServerPing.Builder builder = ping.asBuilder();

            NetworkConfig networkConfig = CloudDriver.getInstance().getConfigManager().getNetworkConfig();
            ProxyConfig proxyConfig = CloudDriver.getInstance().getConfigManager().getProxyConfig();

            if (!proxyConfig.isEnabled()) {
                return;
            }
            Motd motd = CloudBridge.getInstance().loadRandomMotd();
            if (!motd.isEnabled()) {
                return;
            }
            UUID uniqueId = CloudDriver.getInstance().getPermissionPool().getUniqueIdFromIpAddress(event.getConnection().getRemoteAddress());
            if (uniqueId != null) {
                PlayerConnectionObject connectionObject = new PlayerConnectionObject(
                        uniqueId,
                        CloudDriver.getInstance().getPermissionPool().getNameByUUID(uniqueId),
                        event.getConnection().getRemoteAddress().getAddress().getHostAddress(),
                        event.getConnection().getRemoteAddress().getPort(),
                        MinecraftProtocol.valueOf(event.getConnection().getProtocolVersion().getProtocol()),
                        event.getConnection().getProtocolVersion().isUnknown(),
                        event.getConnection().getProtocolVersion().isLegacy()
                );

                CloudDriver.getInstance().getEventManager().callEvent(new DriverEventNetworkPing(connectionObject));

            }
            if (motd.getVersionString() != null && !motd.getVersionString().trim().isEmpty()) {
                builder.version(new ServerPing.Version(2, "§7" + PlaceHolder.apply(motd.getVersionString(), motd)));
            }

            if (motd.getPlayerInfo().length != 0) {

                ServerPing.SamplePlayer[] playerInfos = new ServerPing.SamplePlayer[motd.getPlayerInfo().length];
                for (int i = 0; i < playerInfos.length; i++) {
                    playerInfos[i] = new ServerPing.SamplePlayer(PlaceHolder.apply(motd.getPlayerInfo()[i], motd), UUID.randomUUID());
                }

                builder.samplePlayers(playerInfos);

            }
            builder.maximumPlayers(networkConfig.getMaxPlayers());

            builder.description(Component.text(PlaceHolder.apply(motd.getFirstLine(), motd) + "\n" + PlaceHolder.apply(motd.getSecondLine(), motd)));

            event.setPing(builder.build());
        } catch (Exception e) {
            this.nullPointers++;
            if (nullPointers == 5) {
                System.out.println("[CloudBridge] Couldn't get ProxyPing information for " + this.nullPointers + " times!");
                e.printStackTrace();
                this.nullPointers = 0;
            }
        }

    }

}
