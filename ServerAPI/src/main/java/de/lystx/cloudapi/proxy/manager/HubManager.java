package de.lystx.cloudapi.proxy.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.HubCommandExecuteEvent;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class HubManager {

    private final CloudAPI cloudAPI;

    public HubManager() {
        this.cloudAPI = CloudAPI.getInstance();
    }

    public boolean send(ProxiedPlayer player) {
        if (isFallback(player)) {
            String message = this.cloudAPI.getNetworkConfig().getMessageConfig().getAlreadyHubMessage().replace("%prefix%", CloudAPI.getInstance().getPrefix());
            if (!message.trim().isEmpty()) {
                player.sendMessage(new TextComponent(message));
            }
            return false;
        } else {
            this.sendPlayerToFallback(player);
            return true;
        }
    }

    public ServerInfo getInfo(ProxiedPlayer player) {
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

    public void sendPlayerToFallback(ProxiedPlayer player) {
        if (this.getInfo(player) == null) {
            player.disconnect(CloudAPI.getInstance().getPrefix() + "§cNo fallback was found!");
            return;
        }
        player.connect(this.getInfo(player));
    }

    public Fallback getHighestFallback(ProxiedPlayer player) {
        List<Fallback> list = this.getFallbacks(player);
        list.sort(Comparator.comparingInt(Fallback::getPriority));
        return list.get(list.size() - 1) == null ? cloudAPI.getNetworkConfig().getFallbackConfig().getDefaultFallback() : list.get(list.size() - 1);
    }

    public boolean isFallback(ProxiedPlayer player) {
        for (Fallback fallback : this.getFallbacks(player)) {
            if (player.getServer().getInfo().getName().split("-")[0].equalsIgnoreCase(fallback.getGroupName())) {
                return true;
            }
        }
        return false;
    }


    public List<Fallback> getFallbacks(ProxiedPlayer player) {
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
