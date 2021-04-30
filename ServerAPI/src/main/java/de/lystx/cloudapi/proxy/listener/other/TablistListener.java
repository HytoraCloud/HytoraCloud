package de.lystx.cloudapi.proxy.listener.other;


import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudapi.proxy.events.player.ProxyServerPlayerNetworkJoinEvent;
import de.lystx.cloudapi.proxy.events.player.ProxyServerPlayerNetworkQuitEvent;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketOutUpdateTabList;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.proxy.TabList;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.CloudCache;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.LinkedList;
import java.util.List;

public class TablistListener implements Listener {

    private int tabInits = 0;

    public TablistListener() {
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(this);
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
        CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(player.getName());
        if (cloudPlayer == null) {
            return "";
        }
        try {

            Service service;
            PermissionGroup permissionGroup;
            if (cloudPlayer.getPermissionGroup() == null) {
                permissionGroup = new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new SerializableDocument());
            } else {
                permissionGroup = cloudPlayer.getPermissionGroup();
            }
            if (player.getServer() == null) {
                service = CloudAPI.getInstance().getService();
            } else {
                service = CloudAPI.getInstance().getNetwork().getService(player.getServer().getInfo().getName());
            }
            return string
                    .replace("&", "§")
                    .replace("%max_players%", String.valueOf(CloudProxy.getInstance().getProxyConfig().getMaxPlayers()))
                    .replace("%online_players%", String.valueOf(CloudAPI.getInstance().getCloudPlayers().getAll().size()))
                    .replace("%id%", service.getServiceID() + "")
                    .replace("%group%", service.getServiceGroup().getName() + "")
                    .replace("%rank%", permissionGroup.getName())
                    .replace("%receiver%", CloudAPI.getInstance().getService().getServiceGroup().getReceiver())
                    .replace("%rank_color%", permissionGroup.getDisplay())
                    .replace("%proxy%", CloudAPI.getInstance().getNetwork().getProxy(CloudProxy.getInstance().getProxyPort()).getName())
                    .replace("%server%", service.getName());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the TabList
     */
    public void doUpdate() {
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(this::updateTab, 5L);
    }

    /**
     * Gets a new TabList
     * @return
     */
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
            CloudAPI.getInstance().getScheduler().scheduleRepeatingTask(
                    () -> updateTab(event.getPlayer()),
                    CloudProxy.getInstance().getProxyConfig().getTabListDelay(),
                    CloudProxy.getInstance().getProxyConfig().getTabListDelay()
            );
        }
    }

}
