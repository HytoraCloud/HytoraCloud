package de.lystx.hytoracloud.bridge.bukkit.events.network;

import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloudServerSubChannelMessageEvent extends Event {


    private static final HandlerList handlerList = new HandlerList();

    private final String channel;
    private final String key;
    private final JsonEntity jsonEntity;

    public CloudServerSubChannelMessageEvent(String channel, String key, JsonEntity jsonEntity) {
        this.channel = channel;
        this.key = key;
        this.jsonEntity = jsonEntity;
    }

    public String getChannel() {
        return channel;
    }

    public JsonEntity getDocument() {
        return jsonEntity;
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
