package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.cloud;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CloudListener implements NetworkHandler {

    /**
     * Notifies all {@link ProxiedPlayer}s
     * on the Network if they have the permission to
     * get notified and if they have enabled it
     *
     * @param state the state
     * @param servername the serverName
     */
    public void notify(int state, String servername) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), "cloudsystem.notify")) {
                return;
            }
            OfflinePlayer playerData = CloudDriver.getInstance().getPermissionPool().getCachedObject(player.getUniqueId());
            if (playerData != null && !playerData.isNotifyServerStart()) {
                return;
            }
            String message = null;
            switch (state){
                case 1:
                    message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServiceStart().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudDriver.getInstance().getPrefix());
                    break;
                case 2:
                    message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServiceStop().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudDriver.getInstance().getPrefix());
                    break;
                case 3:
                    return;

            }
            player.sendMessage(new TextComponent(message));
        }
    }

    @Override
    public void onServerStarted(IService service) {
        this.notify(3, service.getName());
    }

    @Override
    public void onServerQueue(IService service) {
        this.notify(1, service.getName());
    }

    @Override
    public void onServerStop(IService service) {
        this.notify(2, service.getName());
    }

}
