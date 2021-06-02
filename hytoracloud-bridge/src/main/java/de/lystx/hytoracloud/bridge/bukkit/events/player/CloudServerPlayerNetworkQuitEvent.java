package de.lystx.hytoracloud.bridge.bukkit.events.player;

import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
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
