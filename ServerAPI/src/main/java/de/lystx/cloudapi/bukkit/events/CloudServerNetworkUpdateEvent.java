package de.lystx.cloudapi.bukkit.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class CloudServerNetworkUpdateEvent extends Event {


    private static final HandlerList handlers = new HandlerList();


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
