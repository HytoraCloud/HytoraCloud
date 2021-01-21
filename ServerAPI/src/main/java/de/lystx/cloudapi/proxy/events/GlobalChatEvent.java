package de.lystx.cloudapi.proxy.events;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter
public class GlobalChatEvent extends Event {

    private final CloudPlayer player;
    private final String message;

    public GlobalChatEvent(CloudPlayer player, String message) {
        this.player = player;
        this.message = message;
    }
}
