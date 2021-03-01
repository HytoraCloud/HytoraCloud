package de.lystx.cloudapi.bukkit.events.player;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class CloudServerPlayerServerSwitchEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final CloudPlayer cloudPlayer;
    private final String newServer;

    public CloudServerPlayerServerSwitchEvent(CloudPlayer cloudPlayer, String newServer) {
        this.cloudPlayer = cloudPlayer;
        this.newServer = newServer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
