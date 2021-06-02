package de.lystx.hytoracloud.bridge.proxy.events.network;

import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import net.md_5.bungee.api.plugin.Event;

public class ProxyServerSubChannelMessageEvent extends Event {

    private final String channel;
    private final String key;
    private final JsonBuilder jsonBuilder;

    public ProxyServerSubChannelMessageEvent(String channel, String key, JsonBuilder jsonBuilder) {
        this.channel = channel;
        this.key = key;
        this.jsonBuilder = jsonBuilder;
    }

    public String getChannel() {
        return channel;
    }

    public String getKey() {
        return key;
    }

    public JsonBuilder getDocument() {
        return jsonBuilder;
    }
}
