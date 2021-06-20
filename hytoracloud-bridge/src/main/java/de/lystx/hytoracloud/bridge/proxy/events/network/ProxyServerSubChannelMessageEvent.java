package de.lystx.hytoracloud.bridge.proxy.events.network;

import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import net.md_5.bungee.api.plugin.Event;

public class ProxyServerSubChannelMessageEvent extends Event {

    private final String channel;
    private final String key;
    private final JsonEntity jsonEntity;

    public ProxyServerSubChannelMessageEvent(String channel, String key, JsonEntity jsonEntity) {
        this.channel = channel;
        this.key = key;
        this.jsonEntity = jsonEntity;
    }

    public String getChannel() {
        return channel;
    }

    public String getKey() {
        return key;
    }

    public JsonEntity getDocument() {
        return jsonEntity;
    }
}
