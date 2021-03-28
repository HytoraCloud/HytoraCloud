package de.lystx.module.notify.bungee;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class NotifyModuleBungee extends Plugin {

    @Override
    public void onEnable() {
        CloudAPI.getInstance().registerNetworkHandler(new NetworkHandler() {
            @Override
            public void onServerStart(Service service) {
                NotifyModuleBungee.this.notify(3, service.getName());
            }

            @Override
            public void onServerQueue(Service service) {
                NotifyModuleBungee.this.notify(1, service.getName());
            }

            @Override
            public void onServerStop(Service service) {
                NotifyModuleBungee.this.notify(2, service.getName());
            }
        });
    }

    /**
     * Notifies all {@link ProxiedPlayer}s
     * on the Network if they have the permission to
     * get notified and if they have enabled it
     *
     * @param state
     * @param servername
     */
    public void notify(int state, String servername) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!CloudAPI.getInstance().getPermissionPool().hasPermission(player.getName(), "cloudsystem.notify")) {
                return;
            }
            CloudPlayerData playerData = CloudAPI.getInstance().getPermissionPool().getPlayerData(player.getName());
            if (playerData != null && !playerData.isNotifyServerStart()) {
                return;
            }
            String message = null;
            switch (state){
                case 1:
                    message = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getServerStartMessage().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudAPI.getInstance().getPrefix());
                    break;
                case 2:
                    message = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getServerStopMessage().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudAPI.getInstance().getPrefix());
                    break;
                case 3:
                    return;

            }
            player.sendMessage(new TextComponent(message));
        }
    }
}
