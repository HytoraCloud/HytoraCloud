package de.lystx.cloudapi.proxy.listener;


import java.util.UUID;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.elements.other.NetworkHandler;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.proxy.Motd;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
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

            if (!this.cloudAPI.getNetworkConfig().getProxyConfig().isEnabled()) {
                return;
            }
            ServerPing ping = event.getResponse();
            ServerPing.Players players = ping.getPlayers();

            ping.getPlayers().setMax(this.cloudAPI.getNetworkConfig().getProxyConfig().getMaxPlayers());

            Motd motd;
            if (this.cloudAPI.getNetworkConfig().getProxyConfig().isMaintenance()) {
                motd = this.cloudAPI.getNetworkConfig().getProxyConfig().getMotdMaintenance();
            } else {
                motd = this.cloudAPI.getNetworkConfig().getProxyConfig().getMotdNormal();
            }

            if (motd.getVersionString() != null && !motd.getVersionString().trim().isEmpty()) {
                ping.setVersion(new ServerPing.Protocol("§7" + ChatColor.translateAlternateColorCodes('&', this.replace(motd.getVersionString(), event.getConnection().getVirtualHost().getPort())), 2));
            }



            if (motd.getProtocolString() != null && !motd.getProtocolString().trim().isEmpty()) {
                players.setSample(new ServerPing.PlayerInfo[] { new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', this.replace(motd.getProtocolString(), event.getConnection().getVirtualHost().getPort())).replace("||", "\n"), UUID.randomUUID())});
            }

            ping.setDescription(ChatColor.translateAlternateColorCodes('&', this.replace(motd.getFirstLine(), event.getConnection().getVirtualHost().getPort())) + "\n" + ChatColor.translateAlternateColorCodes('&', this.replace(motd.getSecondLine(), event.getConnection().getVirtualHost().getPort())));

            ping.setPlayers(players);
            event.setResponse(ping);
        } catch (NullPointerException e) {}
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
