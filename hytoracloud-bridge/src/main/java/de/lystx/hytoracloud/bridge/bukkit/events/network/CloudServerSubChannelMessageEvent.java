package de.lystx.hytoracloud.bridge.bukkit.events.network;

import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloudServerSubChannelMessageEvent extends Event {


    private static final HandlerList handlerList = new HandlerList();

    private final String channel;
    private final String key;
    private final JsonBuilder jsonBuilder;

    public CloudServerSubChannelMessageEvent(String channel, String key, JsonBuilder jsonBuilder) {
        this.channel = channel;
        this.key = key;
        this.jsonBuilder = jsonBuilder;
    }

    public String getChannel() {
        return channel;
    }

    public JsonBuilder getDocument() {
        return jsonBuilder;
    }

    public String getKey() {
        return key;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }


}
