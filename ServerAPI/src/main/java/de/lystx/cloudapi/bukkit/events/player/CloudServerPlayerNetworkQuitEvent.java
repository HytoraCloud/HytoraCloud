package de.lystx.cloudapi.bukkit.events.player;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class CloudServerPlayerNetworkQuitEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final CloudPlayer cloudPlayer;

    public CloudServerPlayerNetworkQuitEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
