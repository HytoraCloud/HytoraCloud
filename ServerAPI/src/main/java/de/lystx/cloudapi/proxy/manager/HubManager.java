package de.lystx.cloudapi.proxy.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

@Getter
public class HubManager {

    private final CloudAPI cloudAPI;

    public HubManager() {
        this.cloudAPI = CloudAPI.getInstance();
    }

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

    public void sendPlayerToFallback(CloudPlayer player) {
        if (this.getInfo(player) == null) {
            player.kick(CloudAPI.getInstance().getPrefix() + "§cNo fallback was found!");
            return;
        }
        player.connect(this.getInfo(player).getName());
    }

    public Fallback getHighestFallback(CloudPlayer player) {
        List<Fallback> list = this.getFallbacks(player);
        list.sort(Comparator.comparingInt(Fallback::getPriority));
        return list.get(list.size() - 1) == null ? cloudAPI.getNetworkConfig().getFallbackConfig().getDefaultFallback() : list.get(list.size() - 1);
    }

    public boolean isFallback(CloudPlayer player) {
        for (Fallback fallback : this.getFallbacks(player)) {
            if (player.getServerGroup().equalsIgnoreCase(fallback.getGroupName())) {
                return true;
            }
        }
        return false;
    }


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
