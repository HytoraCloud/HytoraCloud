package de.lystx.cloudapi.proxy.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Value;
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
            String message = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getAlreadyHubMessage().replace("%prefix%", CloudAPI.getInstance().getPrefix());
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
            Fallback fallback = this.getHighestFallback(player);
            Service service;
            try {
                service = CloudAPI.getInstance().getNetwork().getServices(CloudAPI.getInstance().getNetwork().getServiceGroup(fallback.getGroupName())).get(new Random().nextInt(CloudAPI.getInstance().getNetwork().getServices(CloudAPI.getInstance().getNetwork().getServiceGroup(fallback.getGroupName())).size()));
            } catch (Exception e){
                service = CloudAPI.getInstance().getNetwork().getService(fallback.getGroupName() + "-1");
            }
            return ProxyServer.getInstance().getServerInfo(service.getName());
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
            proxiedPlayer.disconnect(CloudAPI.getInstance().getPrefix() + "§cNo fallback was found!");
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
        return list.get(list.size() - 1) == null ? CloudAPI.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback() : list.get(list.size() - 1);
    }

    /**
     * Checks if player is fallback
     * @param player
     * @return
     */
    public boolean isFallback(ProxiedPlayer player) {
        Value<Boolean> booleanValue = new Value<>(false);
        this.getFallbacks(player).forEach(fallback -> {
            if (player.getServer().getInfo().getName().split("-")[0].equalsIgnoreCase(fallback.getGroupName())) {
                booleanValue.setValue(true);
            }
        });
        return booleanValue.getValue();
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
        List<Fallback> list = new LinkedList<>();
        list.add(CloudAPI.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback());
        CloudAPI.getInstance().getNetworkConfig().getFallbackConfig().getFallbacks().forEach(fallback -> {
            if (CloudAPI.getInstance().getPermissionPool().hasPermission(player.getName(), fallback.getPermission()) || fallback.getPermission().trim().isEmpty() || fallback.getPermission() == null) {
                list.add(fallback);
            }
        });
        return list;
    }

}
