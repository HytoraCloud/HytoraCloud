package de.lystx.cloudapi.proxy.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NetworkManager {


    public void switchMaintenance(Boolean to) {
        if (to) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (CloudAPI.getInstance().getNetworkConfig().getProxyConfig().isMaintenance() && !CloudAPI.getInstance().getNetworkConfig().getProxyConfig().getWhitelistedPlayers().contains(player.getName()) && !player.hasPermission("cloudsystem.network.maintenance")) {
                    player.disconnect(
                            new TextComponent(
                                    CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getMaintenanceKickMessage().
                                            replace("%prefix%", CloudAPI.getInstance().getPrefix()
                                            )
                            )
                    );
                }
            }
        }
    }


    public void sendStartServerMessage(ProxiedPlayer player, String servername) {
        if (!player.hasPermission("cloudsystem.notify")) {
            return;
        }
        CloudPlayerData playerData = CloudAPI.getInstance().getPermissionPool().getPlayerData(player.getName());
        if (playerData != null && !playerData.isNotifyServerStart()) {
            return;
        }
        String stopMessage = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getServerStartMessage().
                replace("&", "§").
                replace("%server%", servername).
                replace("%prefix%", CloudAPI.getInstance().getPrefix());
        player.sendMessage(new TextComponent(stopMessage));
    }

    public void sendStopServerMessage(ProxiedPlayer player, String servername) {
        if (!player.hasPermission("cloudsystem.notify")) {
            return;
        }
        CloudPlayerData playerData = CloudAPI.getInstance().getPermissionPool().getPlayerData(player.getName());
        if (playerData != null && !playerData.isNotifyServerStart()) {
            return;
        }
        ProxyServer.getInstance().getServers().remove(servername);
        String stopMessage = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getServerStopMessage().
                replace("&", "§").
                replace("%server%", servername).
                replace("%prefix%", CloudAPI.getInstance().getPrefix());
        player.sendMessage(new TextComponent(stopMessage));
    }



}
