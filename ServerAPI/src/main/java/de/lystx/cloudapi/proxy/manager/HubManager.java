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

    public void send(ProxiedPlayer player) {
        HubCommandExecuteEvent.Result result;
        if (isFallback(player)) {
            result = HubCommandExecuteEvent.Result.ALREADY_ON_LOBBY;
            String message = this.cloudAPI.getNetworkConfig().getMessageConfig().getAlreadyHubMessage().replace("%prefix%", CloudAPI.getInstance().getPrefix());
            if (message.trim().isEmpty()) {
                return;
            }
            player.sendMessage(new TextComponent(message));
        } else {
            result = HubCommandExecuteEvent.Result.SUCCESS;
            this.sendPlayerToFallback(player);
        }
        ProxyServer.getInstance().getPluginManager().callEvent(new HubCommandExecuteEvent(player, result));
    }

    public ServerInfo getInfo(ProxiedPlayer player) {
        Fallback fallback = this.getHighestFallback(player);
        Service service = cloudAPI.getNetwork().getServices(cloudAPI.getNetwork().getServiceGroup(fallback.getGroupName())).get(new Random().nextInt(cloudAPI.getNetwork().getServices(cloudAPI.getNetwork().getServiceGroup(fallback.getGroupName())).size()));
        return ProxyServer.getInstance().getServerInfo(service.getName());
    }

    public void sendPlayerToFallback(ProxiedPlayer player) {

        player.connect(
                this.getInfo(player)
        );
    }

    public Fallback getHighestFallback(ProxiedPlayer player) {
        List<Fallback> list = this.getFallbacks(player);
        list.sort(Comparator.comparingInt(Fallback::getPriority));

        return list.get(0);
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
            if (player.hasPermission(fallback.getPermission()) || fallback.getPermission().trim().isEmpty() || fallback.getPermission() == null) {
                list.add(fallback);
            }
        }
        return list;
    }

}
