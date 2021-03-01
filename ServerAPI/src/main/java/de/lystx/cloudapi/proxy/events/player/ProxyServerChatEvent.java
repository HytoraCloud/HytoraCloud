package de.lystx.cloudapi.proxy.events.player;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter
public class ProxyServerChatEvent extends Event {

    private final CloudPlayer player;
    private final String message;

    public ProxyServerChatEvent(CloudPlayer player, String message) {
        this.player = player;
        this.message = message;
    }
}
