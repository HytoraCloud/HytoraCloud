package de.lystx.cloudapi.proxy.listener;


import java.util.UUID;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.proxy.Motd;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@Getter
public class ProxyPingListener implements Listener {


    private final CloudAPI cloudAPI;

    public ProxyPingListener() {
        this.cloudAPI = CloudAPI.getInstance();
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        try {

            for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
                networkHandler.onNetworkPing(event.getConnection().getUniqueId());
            }

            if (!this
                    .cloudAPI
                    .getNetworkConfig()
                    .getProxyConfig()
                    .isEnabled()) {
                return;
            }
            int port = event.getConnection().getVirtualHost().getPort();
            ServerPing ping = event.getResponse();
            Motd motd;
            if (this
                    .cloudAPI
                    .getNetworkConfig()
                    .getProxyConfig()
                    .isMaintenance()) {
                motd = this
                        .cloudAPI
                        .getNetworkConfig()
                        .getProxyConfig()
                        .getMotdMaintenance();
            } else {
                motd = this
                        .cloudAPI
                        .getNetworkConfig()
                        .getProxyConfig()
                        .getMotdNormal();
            }

            if (motd.getVersionString() != null && !motd.getVersionString().trim().isEmpty()) {
                ping.setVersion(new ServerPing.Protocol("§7" + ChatColor.translateAlternateColorCodes('&', this.replace(motd.getVersionString(), port)), 2));
            }

            if (motd.getProtocolString() != null && !motd.getProtocolString().trim().isEmpty()) {
                String[] playerInfo = (motd.getProtocolString().replace("||", "-_-")).split("-_-");

                ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[playerInfo.length];
                for (short i = 0; i < playerInfos.length; i++) {
                    playerInfos[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', this.replace(playerInfo[i].replace("-_-", ""), port)), UUID.randomUUID());
                }
                ping.setPlayers(new ServerPing.Players(this.cloudAPI.getNetworkConfig().getProxyConfig().getMaxPlayers(), CloudAPI.getInstance().getCloudPlayers().getAll().size(), playerInfos));

            }
            ping.getPlayers().setMax(this.cloudAPI.getNetworkConfig().getProxyConfig().getMaxPlayers());

            ping.setDescription(ChatColor.translateAlternateColorCodes('&', this.replace(motd.getFirstLine(), port)) + "\n" + ChatColor.translateAlternateColorCodes('&', this.replace(motd.getSecondLine(), port)));

            event.setResponse(ping);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }




    public String replace(String string, int port) {
        Service service = CloudAPI.getInstance().getNetwork().getProxy(port);
        return string
                .replace("%max_players%", String.valueOf(CloudAPI.getInstance().getNetworkConfig().getProxyConfig().getMaxPlayers()))
                .replace("%online_players%", String.valueOf(CloudAPI.getInstance().getCloudPlayers().getAll().size()))
                .replace("%proxy%", service == null ? "no_proxy_available" : service.getName())
                .replace("%maintenance%", String.valueOf(CloudAPI.getInstance().getNetworkConfig().getProxyConfig().isMaintenance()));
    }
}
