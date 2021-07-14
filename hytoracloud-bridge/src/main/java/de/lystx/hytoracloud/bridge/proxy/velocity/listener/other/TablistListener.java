package de.lystx.hytoracloud.bridge.proxy.velocity.listener.other;


import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutUpdateTabList;

public class TablistListener {
    
    public TablistListener() {
        CloudDriver.getInstance().registerPacketHandler(packet -> {
            if (packet instanceof PacketOutUpdateTabList) {
                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().updateTabList(), 5L);
            }
        });
    }

    @Subscribe
    public void on(ServerConnectedEvent e) {
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().updateTabList(), 5L);
    }

    @Subscribe
    public void login(PostLoginEvent event) {
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().updateTabList(), 5L);
    }

    @Subscribe
    public void on(DisconnectEvent event) {
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().updateTabList(), 5L);
    }

    @Subscribe
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
