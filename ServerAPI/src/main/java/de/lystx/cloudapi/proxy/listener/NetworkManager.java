package de.lystx.cloudapi.proxy.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NetworkManager {


    public void switchMaintenance(Boolean to) {
        if (!to) {
            return;
        }
        for (CloudPlayer cloudPlayer : CloudAPI.getInstance().getCloudPlayers()) {
            if (!CloudAPI.getInstance().getNetworkConfig().getNetworkConfig().getWhitelistedPlayers().contains(cloudPlayer.getName()) && !cloudPlayer.hasPermission("cloudsystem.network.maintenance")) {
                cloudPlayer.createConnection().disconnect(
                        CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getMaintenanceKickMessage().replace("%prefix%", CloudAPI.getInstance().getPrefix()));
            }
        }
    }


    public void sendStartServerMessage(ProxiedPlayer player, String servername) {
        if (!CloudAPI.getInstance().getPermissionPool().hasPermission(player.getName(), "cloudsystem.notify")) {
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
        if (!CloudAPI.getInstance().getPermissionPool().hasPermission(player.getName(), "cloudsystem.notify")) {
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
