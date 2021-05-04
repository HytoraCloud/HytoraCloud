package de.lystx.cloudapi.proxy.impl.listener.other;


import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.other.ProxyServerMotdRequestEvent;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.proxy.Motd;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@Getter
public class ProxyPingListener implements Listener {


    private int nullPointers;
    private int pings;

    public ProxyPingListener() {
        this.nullPointers = 0;
        this.pings = 0;
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        try {
            int port = event.getConnection().getVirtualHost().getPort();
            ServerPing ping = event.getResponse();

            ProxyConfig proxyConfig = CloudAPI.getInstance().getService().getServiceGroup().getValues().has("proxyConfig") ? CloudAPI.getInstance().getService().getServiceGroup().getValues().toDocument().getObject("proxyConfig", ProxyConfig.class) : ProxyConfig.defaultConfig();
            if (!proxyConfig.isEnabled()) {
                return;
            }
            Motd motd = this.newMotd();
            if (!motd.isEnabled()) {
                return;
            }
            UUID uniqueId = CloudAPI.getInstance().getPermissionPool().fromIP(event.getConnection().getAddress().getAddress().getHostAddress());
            if (uniqueId != null) {
                CloudConnection cloudConnection = new CloudConnection(
                        uniqueId,
                        CloudAPI.getInstance().getPermissionPool().tryName(uniqueId),
                        event.getConnection().getAddress().getAddress().getHostAddress()
                );
                CloudAPI.getInstance().getCloudClient().getNetworkHandlers().forEach(networkHandler -> networkHandler.onNetworkPing(cloudConnection));

                ProxyServerMotdRequestEvent proxyServerMotdRequestEvent = ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerMotdRequestEvent(cloudConnection));
                if (proxyServerMotdRequestEvent.getMotd() != null) {
                    motd = proxyServerMotdRequestEvent.getMotd();
                }
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
                ping.setPlayers(new ServerPing.Players(proxyConfig.getMaxPlayers(), CloudAPI.getInstance().getCloudPlayers().getAll().size(), playerInfos));

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

    public Motd newMotd() {
        Motd motd;
        List<Motd> motds;
        if (CloudAPI.getInstance().getNetworkConfig().getNetworkConfig().isMaintenance()) {
            motds = CloudAPI.getInstance().getProxyConfig().getMotdMaintenance();
        } else {
            motds = CloudAPI.getInstance().getProxyConfig().getMotdNormal();
        }

        try {
            motd = motds.get(this.pings);
            this.pings++;
        } catch (Exception e) {
            this.pings = 0;
            motd = motds.get(this.pings);
        }
        return motd;
    }

    public String replace(String string, int port) {
        Service service = CloudAPI.getInstance().getNetwork().getProxy(port);
        return string
                .replace("%max_players%", String.valueOf(CloudAPI.getInstance().getProxyConfig().getMaxPlayers()))
                .replace("%online_players%", String.valueOf(CloudAPI.getInstance().getCloudPlayers().getAll().size()))
                .replace("%proxy%", service == null ? "no_proxy_available" : service.getName())
                .replace("%maintenance%", String.valueOf(CloudAPI.getInstance().getNetworkConfig().getNetworkConfig().isMaintenance()));
    }
}
