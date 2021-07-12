package de.lystx.hytoracloud.bridge.velocity.listener.other;


import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.events.network.DriverEventNetworkPing;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.UUID;
import java.util.stream.IntStream;

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

            ProxyConfig proxyConfig = CloudDriver.getInstance().getCurrentService().getGroup().getProperties().has("proxyConfig") ? CloudDriver.getInstance().getCurrentService().getGroup().getProperties().toDocument().getObject("proxyConfig", ProxyConfig.class) : ProxyConfig.defaultConfig();
            if (!proxyConfig.isEnabled()) {
                return;
            }
            Motd motd = CloudBridge.getInstance().loadRandomMotd();
            if (!motd.isEnabled()) {
                return;
            }
            UUID uniqueId = CloudDriver.getInstance().getPermissionPool().getUniqueIdFromIpAddress(event.getConnection().getRemoteAddress());
            if (uniqueId != null) {
                PlayerConnection playerConnection = new PlayerConnection(
                        uniqueId,
                        CloudDriver.getInstance().getPermissionPool().getNameByUUID(uniqueId),
                        event.getConnection().getRemoteAddress().getAddress().getHostAddress(),
                        event.getConnection().getProtocolVersion().getProtocol(),
                        event.getConnection().getProtocolVersion().isUnknown(),
                        event.getConnection().getProtocolVersion().isLegacy()
                );

                CloudDriver.getInstance().callEvent(new DriverEventNetworkPing(playerConnection));

            }
            if (motd.getVersionString() != null && !motd.getVersionString().trim().isEmpty()) {
                builder.version(new ServerPing.Version(2, "§7" + this.replace(motd.getVersionString(), port)));
            }

            if (motd.getProtocolString() != null && !motd.getProtocolString().trim().isEmpty()) {
                String[] playerInfo = (motd.getProtocolString().replace("||", "-_-")).split("-_-");

                ServerPing.SamplePlayer[] playerInfos = new ServerPing.SamplePlayer[playerInfo.length];
                IntStream.range(0, playerInfos.length).forEach(i -> playerInfos[i] = new ServerPing.SamplePlayer(this.replace(playerInfo[i].replace("-_-", ""), port), UUID.randomUUID()));

                builder.samplePlayers(playerInfos);

            }
            builder.maximumPlayers(proxyConfig.getMaxPlayers());

            builder.description(Component.text(this.replace(motd.getFirstLine(), port) + "\n" + this.replace(motd.getSecondLine(), port)));

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

    public String replace(String string, int port) {
        IService IService = CloudDriver.getInstance().getServiceManager().getProxy(port);
        return (string
                .replace("%max_players%", String.valueOf(CloudDriver.getInstance().getProxyConfig().getMaxPlayers()))
                .replace("%online_players%", String.valueOf(CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size()))
                .replace("%proxy%", IService == null ? "NO-PROXY-FOUND" : IService.getName())
                .replace("%maintenance%", String.valueOf(CloudDriver.getInstance().getNetworkConfig().isMaintenance()))).replace("&", "§");
    }
}
