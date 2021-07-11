package de.lystx.hytoracloud.bridge.standalone.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketChannelMessage;
import de.lystx.hytoracloud.driver.service.global.messenger.ChannelMessage;
import de.lystx.hytoracloud.driver.service.global.messenger.ChannelMessageListener;
import de.lystx.hytoracloud.driver.service.global.messenger.IChannelMessenger;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CloudBridgeChannelMessenger implements IChannelMessenger {

    private final Map<String, List<ChannelMessageListener>> listeners;

    public CloudBridgeChannelMessenger() {
        this.listeners = new HashMap<>();
    }

    @Override
    public List<ChannelMessageListener> getAllListeners() {
        List<ChannelMessageListener> listeners = new ArrayList<>();
        for (List<ChannelMessageListener> value : this.listeners.values()) {
            listeners.addAll(value);
        }
        return listeners;
    }

    @Override
    public List<ChannelMessageListener> getListenersOfChannel(String channel) {
        return this.listeners.getOrDefault(channel, new ArrayList<>());
    }

    @Override
    public void registerChannelListener(String channel, ChannelMessageListener listener) {
        List<ChannelMessageListener> listeners = this.getListenersOfChannel(channel);
        listeners.add(listener);
        this.listeners.put(channel, listeners);
    }

    @Override
    public void unregisterChannelListener(ChannelMessageListener listener) {
        for (String channel : this.listeners.keySet()) {
            List<ChannelMessageListener> listeners = this.getListenersOfChannel(channel);
            listeners.remove(listener);
            this.listeners.put(channel, listeners);
        }
    }

    @Override
    public void sendChannelMessage(ChannelMessage channelMessage) {
        CloudDriver.getInstance().sendPacket(new PacketChannelMessage(channelMessage));
    }
}
