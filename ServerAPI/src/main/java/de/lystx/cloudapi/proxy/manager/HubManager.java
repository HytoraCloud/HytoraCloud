package de.lystx.cloudapi.proxy.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class HubManager {

    private final CloudAPI cloudAPI;

    public HubManager() {
        this.cloudAPI = CloudAPI.getInstance();
    }

    public void send(ProxiedPlayer player) {
        if (isFallback(player)) {
            String message = this.cloudAPI.getNetworkConfig().getMessageConfig().getAlreadyHubMessage().replace("%prefix%", CloudAPI.getInstance().getPrefix());
            player.sendMessage(new TextComponent(message));
        } else {
            this.sendPlayerToFallback(player);
        }
    }

    public ServerInfo getInfo(ProxiedPlayer player) {
        ArrayList<String> fallback = new ArrayList<>();
        ProxyServer.getInstance().getServers().forEach((name, server) -> {
            String groupname = name.split("-")[0];
            if (getFallbacks(player).contains(groupname)) {
                fallback.add(name);
            }
        });
        String randomServer;
        if (fallback.size() == 0) {
            randomServer = (this.cloudAPI.getNetworkConfig().getFallbackConfig().getDefaultFallback().getGroupName() + "-1");
        } else {
            try {
                randomServer = fallback.get(ThreadLocalRandom.current().nextInt(0, fallback.size()));
            } catch (IndexOutOfBoundsException e) {
                randomServer = (this.cloudAPI.getNetworkConfig().getFallbackConfig().getDefaultFallback().getGroupName() + "-1");
            }
        }
        return ProxyServer.getInstance().getServerInfo(randomServer);
    }

    public void sendPlayerToFallback(ProxiedPlayer player) {
        player.connect(getInfo(player));
    }


    public List<String> getFallbacks(ProxiedPlayer player) {
        List<String> list = new LinkedList<>();

        ProxyServer.getInstance().getServers().forEach((name, server) -> {
            String groupname = name.split("-")[0];
            Fallback fallback = this.getFallback(groupname);
            if (fallback == null) {
                return;
            }
            if (player.hasPermission(fallback.getPermission()) || fallback.getPermission().equalsIgnoreCase("%none%") || fallback.getPermission() == null || fallback.getPermission().trim().isEmpty()) {
                list.add(fallback.getGroupName());
            }
        });
        if (list.isEmpty()) {
            list.add((this.cloudAPI.getNetworkConfig().getFallbackConfig().getDefaultFallback().getGroupName() + "-1"));
        }
        return list;
    }

    public Fallback getFallback(String name) {
        for (Fallback fallback : this.cloudAPI.getNetworkConfig().getFallbackConfig().getFallbacks()) {
            if (fallback.getGroupName().equalsIgnoreCase(name)) {
                return fallback;
            }
        }
        return null;
    }

    public Boolean isFallback(ProxiedPlayer player) {
        return getInfo(player).getName().split("-")[0].equals(player.getServer().getInfo().getName().split("-")[0]);
    }

}
