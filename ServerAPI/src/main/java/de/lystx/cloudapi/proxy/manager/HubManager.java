package de.lystx.cloudapi.proxy.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

@Getter
public class HubManager {

    private final CloudAPI cloudAPI;

    public HubManager() {
        this.cloudAPI = CloudAPI.getInstance();
    }

    /**
     * Sends player to LobbyServer
     * with hubMessage if
     * already on hub!
     * @param player
     * @return
     */
    public boolean send(CloudPlayer player) {
        if (isFallback(player)) {
            String message = this.cloudAPI.getNetworkConfig().getMessageConfig().getAlreadyHubMessage().replace("%prefix%", CloudAPI.getInstance().getPrefix());
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
    public ServerInfo getInfo(CloudPlayer player) {
        try {
            Fallback fallback = this.getHighestFallback(player);
            Service service;
            try {
                service = cloudAPI.getNetwork().getServices(cloudAPI.getNetwork().getServiceGroup(fallback.getGroupName())).get(new Random().nextInt(cloudAPI.getNetwork().getServices(cloudAPI.getNetwork().getServiceGroup(fallback.getGroupName())).size()));
            } catch (Exception e){
                service = cloudAPI.getNetwork().getService(fallback.getGroupName() + "-1");
            }
            return ProxyServer.getInstance().getServerInfo(service.getName());
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Sends a player to a
     * random Lobby-Server
     * @param player
     */
    public void sendPlayerToFallback(CloudPlayer player) {
        ProxiedPlayer proxiedPlayer =  ProxyServer.getInstance().getPlayer(player.getName());
        if (this.getInfo(player) == null) {
            proxiedPlayer.disconnect(CloudAPI.getInstance().getPrefix() + "§cNo fallback was found!");
            return;
        }
        proxiedPlayer.connect(this.getInfo(player));
    }

    /**
     * Gets Fallback with highest
     * ID (Example sorting 1, 2, 3)
     * @param player
     * @return
     */
    public Fallback getHighestFallback(CloudPlayer player) {
        List<Fallback> list = this.getFallbacks(player);
        list.sort(Comparator.comparingInt(Fallback::getPriority));
        return list.get(list.size() - 1) == null ? cloudAPI.getNetworkConfig().getFallbackConfig().getDefaultFallback() : list.get(list.size() - 1);
    }

    /**
     * Checks if player is fallback
     * @param player
     * @return
     */
    public boolean isFallback(CloudPlayer player) {
        for (Fallback fallback : this.getFallbacks(player)) {
            if (player.getServerGroup().equalsIgnoreCase(fallback.getGroupName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterates through all Fallbacks
     * if permission of fallback is null
     * or player has fallback permission
     * adds it to a list
     * @param player
     * @return
     */
    public List<Fallback> getFallbacks(CloudPlayer player) {
        List<Fallback> list = new LinkedList<>();
        list.add(cloudAPI.getNetworkConfig().getFallbackConfig().getDefaultFallback());
        for (Fallback fallback : cloudAPI.getNetworkConfig().getFallbackConfig().getFallbacks()) {
            if (cloudAPI.getPermissionPool().hasPermission(player.getName(), fallback.getPermission()) || fallback.getPermission().trim().isEmpty() || fallback.getPermission() == null) {
                list.add(fallback);
            }
        }
        return list;
    }

}
