package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.other;


import java.util.UUID;
import java.util.stream.IntStream;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.commons.events.network.DriverEventNetworkPing;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
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


            NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();

            ProxyConfig proxyConfig = CloudDriver.getInstance().getProxyConfig();

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

            }
            if (motd.getVersionString() != null && !motd.getVersionString().trim().isEmpty()) {
                ping.setVersion(new ServerPing.Protocol("§7" + ChatColor.translateAlternateColorCodes('&', this.replace(motd.getVersionString(), port)), 2));
            }

            if (motd.getProtocolString() != null && !motd.getProtocolString().trim().isEmpty()) {
                String[] playerInfo = (motd.getProtocolString().replace("||", "-_-")).split("-_-");

                ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[playerInfo.length];
                IntStream.range(0, playerInfos.length).forEach(i -> playerInfos[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', this.replace(playerInfo[i].replace("-_-", ""), port)), UUID.randomUUID()));
                ping.setPlayers(new ServerPing.Players(networkConfig.getMaxPlayers(), CloudDriver.getInstance().getPlayerManager().getCachedObjects().size(), playerInfos));

            }
            ping.getPlayers().setMax(networkConfig.getMaxPlayers());

            ping.setDescription(ChatColor.translateAlternateColorCodes('&', this.replace(motd.getFirstLine(), port)) + "\n" + ChatColor.translateAlternateColorCodes('&', this.replace(motd.getSecondLine(), port)));

            event.setResponse(ping);
        } catch (NullPointerException e) {
            this.nullPointers++;
            if (nullPointers == 5) {
                System.out.println("[CloudBridge] Couldn't get ProxyPing information for " + this.nullPointers + " times!");
                e.printStackTrace();
                this.nullPointers = 0;
            }
        }

    }

    public String replace(String string, int port) {
        IService service = CloudDriver.getInstance().getServiceManager().getCachedObjects(service1 -> service1.getPort() == port).get(0);
        return string
                .replace("%max_players%", String.valueOf(CloudDriver.getInstance().getNetworkConfig().getMaxPlayers()))
                .replace("%online_players%", String.valueOf(CloudDriver.getInstance().getPlayerManager().getCachedObjects().size()))
                .replace("%proxy%", service == null ? "no_proxy_available" : service.getName())
                .replace("%maintenance%", String.valueOf(CloudDriver.getInstance().getNetworkConfig().isMaintenance()));
    }
}
