package de.lystx.cloudapi.proxy.listener;


import java.util.UUID;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.proxy.Motd;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@Getter
public class ProxyPingListener implements Listener {


    private final CloudAPI cloudAPI;
    private int nullPointers;

    public ProxyPingListener() {
        this.cloudAPI = CloudAPI.getInstance();
        this.nullPointers = 0;
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        try {
            int port = event.getConnection().getVirtualHost().getPort();
            ServerPing ping = event.getResponse();
            if (this.cloudAPI == null) {
                ping.setDescription("§4CloudAPI is null!");
                event.setResponse(ping);
                return;
            }
            if (this.cloudAPI.getNetworkConfig() == null) {
                ping.setDescription("§4NetworkConfig is null!");
                event.setResponse(ping);
                return;
            }
            ProxyConfig proxyConfig = this.cloudAPI.getNetworkConfig().getProxyConfig();
            if (!proxyConfig.isEnabled()) {
                return;
            }
            Motd motd = proxyConfig.isMaintenance() ? proxyConfig.getMotdMaintenance() : proxyConfig.getMotdNormal();
            if (motd.getVersionString() != null && !motd.getVersionString().trim().isEmpty()) {
                ping.setVersion(new ServerPing.Protocol("§7" + ChatColor.translateAlternateColorCodes('&', this.replace(motd.getVersionString(), port)), 2));
            }

            if (motd.getProtocolString() != null && !motd.getProtocolString().trim().isEmpty()) {
                String[] playerInfo = (motd.getProtocolString().replace("||", "-_-")).split("-_-");

                ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[playerInfo.length];
                for (short i = 0; i < playerInfos.length; i++) {
                    playerInfos[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', this.replace(playerInfo[i].replace("-_-", ""), port)), UUID.randomUUID());
                }
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

        UUID uniqueId = CloudAPI.getInstance().getPermissionPool().fromIP(event.getConnection().getAddress().getAddress().getHostAddress());
        if (uniqueId == null) {
            return;
        }
        CloudConnection cloudConnection = new CloudConnection(
                        uniqueId,
                        CloudAPI.getInstance().getPermissionPool().tryName(uniqueId),
                        event.getConnection().getAddress().getAddress().getHostAddress()
                     );
        for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
            networkHandler.onNetworkPing(cloudConnection);
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
