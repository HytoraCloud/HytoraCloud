package de.lystx.hytoracloud.bridge.proxy.impl.listener.other;


import de.lystx.hytoracloud.bridge.proxy.CloudProxy;
import de.lystx.hytoracloud.bridge.proxy.events.player.ProxyServerPlayerNetworkJoinEvent;
import de.lystx.hytoracloud.bridge.proxy.events.player.ProxyServerPlayerNetworkQuitEvent;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutUpdateTabList;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.config.impl.proxy.TabList;

import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TablistListener implements Listener, PacketHandler {

    private int tabInits = 0;

    public TablistListener() {
        CloudDriver.getInstance().registerPacketHandler(this);
    }

    public void updateTab() {
        ProxyServer.getInstance().getPlayers().forEach(this::updateTab);
    }

    public void updateTab(ProxiedPlayer player) {
        TabList tabList = this.newTabList();
        if (!CloudDriver.getInstance().getProxyConfig().isEnabled() || !tabList.isEnabled()) {
            return;
        }
        player.setTabHeader(
                new TextComponent(this.replace(tabList.getHeader(), player)),
                new TextComponent(this.replace(tabList.getFooter(), player))
        );
    }

    public String replace(String string, ProxiedPlayer player) {
        CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());
        if (cloudPlayer == null) {
            return "";
        }
        try {

            Service service;
            PermissionGroup permissionGroup;
            if (cloudPlayer.getPermissionGroup() == null) {
                permissionGroup = new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new HashMap<>());
            } else {
                permissionGroup = cloudPlayer.getCachedPermissionGroup();
            }
            if (player.getServer() == null) {
                service = CloudDriver.getInstance().getThisService();
            } else {
                service = CloudDriver.getInstance().getServiceManager().getService(player.getServer().getInfo().getName());
            }
            assert permissionGroup != null;
            return string
                    .replace("&", "§")
                    .replace("%max_players%", String.valueOf(CloudDriver.getInstance().getProxyConfig().getMaxPlayers()))
                    .replace("%online_players%", String.valueOf(CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size()))
                    .replace("%id%", service.getServiceID() + "")
                    .replace("%group%", service.getServiceGroup().getName() + "")
                    .replace("%rank%", permissionGroup.getName())
                    .replace("%receiver%", CloudDriver.getInstance().getThisService().getServiceGroup().getReceiver())
                    .replace("%rank_color%", permissionGroup.getDisplay())
                    .replace("%proxy%", CloudDriver.getInstance().getServiceManager().getProxy(CloudProxy.getInstance().getProxyPort()).getName())
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
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(this::updateTab, 5L);
    }

    /**
     * Gets a new TabList
     * @return
     */
    public TabList newTabList() {
        TabList tabList;
        List<TabList> tabLists = CloudDriver.getInstance().getProxyConfig().getTabList();
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

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketOutUpdateTabList) {
            this.doUpdate();
        }
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
        if (CloudDriver.getInstance().getProxyConfig().getTabList().size() > 1) {
            CloudDriver.getInstance().getScheduler().scheduleRepeatingTask(
                    () -> updateTab(event.getPlayer()),
                    CloudDriver.getInstance().getProxyConfig().getTabListDelay(),
                    CloudDriver.getInstance().getProxyConfig().getTabListDelay()
            );
        }
    }

}
