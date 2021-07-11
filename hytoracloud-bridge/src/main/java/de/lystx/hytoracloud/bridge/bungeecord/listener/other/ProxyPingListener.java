package de.lystx.hytoracloud.bridge.bungeecord.listener.other;


import java.util.UUID;
import java.util.stream.IntStream;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.events.network.DriverEventNetworkPing;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.service.global.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.service.global.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.service.managing.player.impl.PlayerConnection;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@Getter
public class ProxyPingListener implements Listener {


    private int nullPointers;

    public ProxyPingListener() {
        this.nullPointers = 0;
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        try {
            int port = event.getConnection().getVirtualHost().getPort();
            ServerPing ping = event.getResponse();

            ProxyConfig proxyConfig = CloudDriver.getInstance().getThisService().getServiceGroup().getProperties().has("proxyConfig") ? CloudDriver.getInstance().getThisService().getServiceGroup().getProperties().toDocument().getObject("proxyConfig", ProxyConfig.class) : ProxyConfig.defaultConfig();
            if (!proxyConfig.isEnabled()) {
                return;
            }
            Motd motd = CloudBridge.getInstance().loadRandomMotd();
            if (!motd.isEnabled()) {
                return;
            }
            UUID uniqueId = CloudDriver.getInstance().getPermissionPool().getUniqueIdFromIpAddress(event.getConnection().getAddress());
            if (uniqueId != null) {
                PlayerConnection playerConnection = new PlayerConnection(
                        uniqueId,
                        CloudDriver.getInstance().getPermissionPool().getNameByUUID(uniqueId),
                        event.getConnection().getAddress().getAddress().getHostAddress(),
                        event.getConnection().getVersion(),
                        event.getConnection().isOnlineMode(),
                        event.getConnection().isLegacy()
                );

                CloudDriver.getInstance().callEvent(new DriverEventNetworkPing(playerConnection));
                CloudDriver.getInstance().getNetworkHandlers().forEach(networkHandler -> networkHandler.onNetworkPing(playerConnection));

            }
            if (motd.getVersionString() != null && !motd.getVersionString().trim().isEmpty()) {
                ping.setVersion(new ServerPing.Protocol("§7" + ChatColor.translateAlternateColorCodes('&', this.replace(motd.getVersionString(), port)), 2));
            }

            if (motd.getProtocolString() != null && !motd.getProtocolString().trim().isEmpty()) {
                String[] playerInfo = (motd.getProtocolString().replace("||", "-_-")).split("-_-");

                ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[playerInfo.length];
                IntStream.range(0, playerInfos.length).forEach(i -> {
                    playerInfos[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', this.replace(playerInfo[i].replace("-_-", ""), port)), UUID.randomUUID());
                });
                ping.setPlayers(new ServerPing.Players(proxyConfig.getMaxPlayers(), CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size(), playerInfos));

            }
            ping.getPlayers().setMax(proxyConfig.getMaxPlayers());

            ping.setDescription(ChatColor.translateAlternateColorCodes('&', this.replace(motd.getFirstLine(), port)) + "\n" + ChatColor.translateAlternateColorCodes('&', this.replace(motd.getSecondLine(), port)));

            event.setResponse(ping);
        } catch (NullPointerException e) {
            this.nullPointers++;
            if (nullPointers == 5) {
                System.out.println("[CloudProxy] Couldn't get ProxyPing information for " + this.nullPointers + " times!");
                e.printStackTrace();
                this.nullPointers = 0;
            }
        }

    }

    public String replace(String string, int port) {
        Service service = CloudDriver.getInstance().getServiceManager().getProxy(port);
        return string
                .replace("%max_players%", String.valueOf(CloudDriver.getInstance().getProxyConfig().getMaxPlayers()))
                .replace("%online_players%", String.valueOf(CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size()))
                .replace("%proxy%", service == null ? "no_proxy_available" : service.getName())
                .replace("%maintenance%", String.valueOf(CloudDriver.getInstance().getNetworkConfig().getGlobalProxyConfig().isMaintenance()));
    }
}
