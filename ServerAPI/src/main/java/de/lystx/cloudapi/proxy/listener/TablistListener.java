package de.lystx.cloudapi.proxy.listener;


import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudapi.proxy.events.player.ProxyServerPlayerNetworkJoinEvent;
import de.lystx.cloudapi.proxy.events.player.ProxyServerPlayerNetworkQuitEvent;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketOutUpdateTabList;
import de.lystx.cloudsystem.library.service.config.impl.proxy.TabList;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.util.Value;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class TablistListener implements Listener {

    private final CloudAPI cloudAPI;
    private int tabInits = 0;

    public TablistListener() {
        this.cloudAPI = CloudAPI.getInstance();

        this.cloudAPI.getCloudClient().registerPacketHandler(this);
    }

    public void updateTab() {
        ProxyServer.getInstance().getPlayers().forEach(this::updateTab);
    }

    public void updateTab(ProxiedPlayer player) {
        TabList tabList = this.newTabList();
        if (!CloudProxy.getInstance().getProxyConfig().isEnabled() || !tabList.isEnabled()) {
            return;
        }
        player.setTabHeader(
                new TextComponent(this.replace(tabList.getHeader(), player)),
                new TextComponent(this.replace(tabList.getFooter(), player))
        );
    }

    public String replace(String string, ProxiedPlayer player) {
        Value<String> stringValue = new Value<>(string);
        int i = CloudAPI.getInstance().getCloudPlayers().getAll().size();
        try {
            String server = player.getServer() == null ? "not_available" : player.getServer().getInfo().getName();
            stringValue.setValue(string
                    .replace("&", "§")
                    .replace("%max_players%", String.valueOf(CloudProxy.getInstance().getProxyConfig().getMaxPlayers()))
                    .replace("%online_players%", String.valueOf(i))
                    .replace("%id%", this.cloudAPI.getNetwork().getService(player.getServer().getInfo().getName()).getServiceID() + "")
                    .replace("%group%", this.cloudAPI.getNetwork().getService(player.getServer().getInfo().getName()).getServiceGroup().getName() + "")
                    .replace("%rank%", this.cloudAPI.getPermissionPool().getHighestPermissionGroup(player.getName()).getName())
                    .replace("%rank_color%", this.cloudAPI.getPermissionPool().getHighestPermissionGroup(player.getName()).getDisplay())
                    .replace("%proxy%", CloudAPI.getInstance().getNetwork().getProxy(
                            CloudProxy.getInstance().getProxyPort()
                    ).getName())
                    .replace("%server%", server)
                    .replace("%maintenance%", String.valueOf(this.cloudAPI.getNetworkConfig().getNetworkConfig().isMaintenance())));
        } catch (NullPointerException e) {}
        return stringValue.getValue();
    }

    public void doUpdate() {
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(this::updateTab, 5L);
    }

    public TabList newTabList() {
        TabList tabList;
        List<TabList> tabLists = CloudProxy.getInstance().getProxyConfig().getTabList();
        if (tabLists.size() == 1) {
            return tabLists.get(0);
        }
        try {
            tabList = tabLists.get(this.tabInits);
            this.tabInits++;
        } catch (Exception e) {
            this.tabInits = 0;
            tabList = tabLists.get(this.tabInits);
        }
        return tabList;
    }

    @PacketHandler
    public void handle(PacketOutUpdateTabList packet) {
        this.doUpdate();
    }

    @EventHandler
    public void on(ServerConnectedEvent e) {
        this.doUpdate();
    }

    @EventHandler
    public void on(ProxyServerPlayerNetworkJoinEvent event) {
        this.doUpdate();
    }

    @EventHandler
    public void on(ProxyServerPlayerNetworkQuitEvent event) {
        this.doUpdate();
    }

    @EventHandler
    public void on(PostLoginEvent event) {
        if (CloudProxy.getInstance().getProxyConfig().getTabList().size() > 1) {
            this.cloudAPI.getScheduler().scheduleRepeatingTask(
                    () -> updateTab(event.getPlayer()),
                    CloudProxy.getInstance().getProxyConfig().getTabListDelay(),
                    CloudProxy.getInstance().getProxyConfig().getTabListDelay()
            );
        }
    }

}
