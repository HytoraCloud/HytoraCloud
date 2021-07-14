package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.other;


import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutUpdateTabList;

import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TablistListener implements Listener {
    
    public TablistListener() {
        CloudDriver.getInstance().registerPacketHandler(packet -> {
            if (packet instanceof PacketOutUpdateTabList) {
                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().updateTabList(), 5L);
            }
        });
    }

    @EventHandler
    public void on(ServerConnectedEvent e) {
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().updateTabList(), 5L);
    }

    @EventHandler
    public void login(PostLoginEvent event) {
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().updateTabList(), 5L);
    }

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().updateTabList(), 5L);
    }

    @EventHandler
    public void on(PostLoginEvent event) {
        ProxyConfig orDefault = CloudDriver.getInstance().getProxyConfig();
        if (orDefault.getTabList().size() > 1) {
            CloudDriver.getInstance().getScheduler().scheduleRepeatingTask(
                    () -> CloudBridge.getInstance().getProxyBridge().updateTabList(),
                    orDefault.getTabListDelay(),
                    orDefault.getTabListDelay()
            );
        }
    }

}
