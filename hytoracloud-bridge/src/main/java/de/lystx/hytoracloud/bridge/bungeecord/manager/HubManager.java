package de.lystx.hytoracloud.bridge.bungeecord.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.global.config.impl.fallback.Fallback;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

@Getter
public class HubManager {

    /**
     * Sends player to LobbyServer
     * with hubMessage if
     * already on hub!
     * @param player
     * @return
     */
    public boolean send(ProxiedPlayer player) {
        if (isFallback(player)) {
            String message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getAlreadyHubMessage().replace("%prefix%", CloudDriver.getInstance().getCloudPrefix());
            if (!message.trim().isEmpty()) {
                player.sendMessage(message);
            }
            return false;
        } else {
            this.sendPlayerToFallback(player);
            return true;
        }
    }

    /**
     * Returns {@link ServerInfo} of
     * Fallback for {@link CloudPlayer}
     * @param player
     * @return
     */
    public ServerInfo getInfo(ProxiedPlayer player) {
        try {
            return ProxyServer.getInstance().getServerInfo(CloudDriver.getInstance().getFallback(
                    CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName())
            ).getName());
        } catch (NullPointerException e) {
            return null;
        }
    }


    /**
     * Sends a player to a
     * random Lobby-Server
     * @param proxiedPlayer
     */
    public void sendPlayerToFallback(ProxiedPlayer proxiedPlayer) {
        if (this.getInfo(proxiedPlayer) == null) {
            proxiedPlayer.disconnect(CloudDriver.getInstance().getCloudPrefix() + "§cNo fallback was found!");
            return;
        }
        proxiedPlayer.connect(this.getInfo(proxiedPlayer));
    }

    /**
     * Gets Fallback with highest
     * ID (Example sorting 1, 2, 3)
     * @param player
     * @return
     */
    public Fallback getHighestFallback(ProxiedPlayer player) {
        List<Fallback> list = this.getFallbacks(player);
        list.sort(Comparator.comparingInt(Fallback::getPriority));
        return list.get(list.size() - 1) == null ? CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback() : list.get(list.size() - 1);
    }

    /**
     * Checks if player is fallback
     * @param player
     * @return
     */
    public boolean isFallback(ProxiedPlayer player) {
        final CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());
        return CloudDriver.getInstance().isFallback(cloudPlayer);
    }

    /**
     * Iterates through all Fallbacks
     * if permission of fallback is null
     * or player has fallback permission
     * adds it to a list
     * @param player
     * @return
     */
    public List<Fallback> getFallbacks(ProxiedPlayer player) {
        final CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());
        return CloudDriver.getInstance().getFallbacks(cloudPlayer);
    }

}
