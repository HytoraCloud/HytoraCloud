package de.lystx.cloudapi.proxy.listener;


import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.config.impl.proxy.TabList;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Value;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class TablistListener implements Listener {

    private final CloudAPI cloudAPI;

    public TablistListener() {
        this.cloudAPI = CloudAPI.getInstance();
        this.cloudAPI.getCloudClient().registerHandler(new NetworkHandler() {
            @Override
            public void onServerStart(Service service) {}

            @Override
            public void onServerQueue(Service service) { }

            @Override
            public void onServerStop(Service service) {}

            @Override
            public void onServerUpdate(Service service) {}

            @Override
            public void onGroupUpdate(ServiceGroup group) { }

            @Override
            public void onPlayerJoin(CloudPlayer cloudPlayer) {
                doUpdate();
            }

            @Override
            public void onServerChange(CloudPlayer cloudPlayer, String server) {}

            @Override
            public void onPlayerQuit(CloudPlayer cloudPlayer) {
                doUpdate();
            }

            @Override
            public void onNetworkPing(UUID connectionUUID) {}

            @Override
            public void onDocumentReceive(String channel, String key, Document document, ServiceType type) {}
        });
    }

    public void updateTab() {
        TabList tabList = this.cloudAPI.getNetworkConfig().getProxyConfig().getTabList();
        if (!this.cloudAPI.getNetworkConfig().getProxyConfig().isEnabled() || !tabList.isEnabled()) {
            return;
        }
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.setTabHeader(
                    new TextComponent(this.replace(tabList.getHeader(), player)),
                    new TextComponent(this.replace(tabList.getFooter(), player))
            );
        }

    }

    public String replace(String string, ProxiedPlayer player) {
        Value<String> stringValue = new Value<>(string);
        int i = CloudAPI.getInstance().getCloudPlayers().getAll().size();
        try {
            String server = player.getServer() == null ? "not_available" : player.getServer().getInfo().getName();
            stringValue.set(string
                    .replace("&", "§")
                    .replace("%max_players%", String.valueOf(CloudAPI.getInstance().getNetworkConfig().getProxyConfig().getMaxPlayers()))
                    .replace("%online_players%", String.valueOf(i))
                    .replace("%proxy%", CloudAPI.getInstance().getNetwork().getProxy(
                            CloudProxy.getInstance().getProxyPort()
                    ).getName())
                    .replace("%server%", server)
                    .replace("%maintenance%", String.valueOf(CloudAPI.getInstance().getNetworkConfig().getProxyConfig().isMaintenance())));
        } catch (NullPointerException e) {}
        return stringValue.get();
    }

    public void doUpdate() {
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(this::updateTab, 5L);
    }

    @EventHandler
    public void on(ServerConnectedEvent e) {
        this.doUpdate();
    }


    @EventHandler
    public void on(PlayerDisconnectEvent e) {
        this.doUpdate();
    }

}
