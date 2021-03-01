package de.lystx.cloudapi.proxy.events.network;

import de.lystx.cloudsystem.library.elements.other.Document;
import net.md_5.bungee.api.plugin.Event;

public class ProxyServerSubChannelMessageEvent extends Event {

    private final String channel;
    private final String key;
    private final Document document;

    public ProxyServerSubChannelMessageEvent(String channel, String key, Document document) {
        this.channel = channel;
        this.key = key;
        this.document = document;
    }

    public String getChannel() {
        return channel;
    }

    public String getKey() {
        return key;
    }

    public Document getDocument() {
        return document;
    }
}
