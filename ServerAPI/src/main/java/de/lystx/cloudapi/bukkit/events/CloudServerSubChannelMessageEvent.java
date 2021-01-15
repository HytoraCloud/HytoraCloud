package de.lystx.cloudapi.bukkit.events;

import de.lystx.cloudsystem.library.elements.other.Document;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloudServerSubChannelMessageEvent extends Event {


    private static final HandlerList handlerList = new HandlerList();

    private final String channel;
    private final String key;
    private final Document document;

    public CloudServerSubChannelMessageEvent(String channel, String key, Document document) {
        this.channel = channel;
        this.key = key;
        this.document = document;
    }

    public String getChannel() {
        return channel;
    }

    public Document getDocument() {
        return document;
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
