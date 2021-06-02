package de.lystx.hytoracloud.bridge.bukkit.events.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter @AllArgsConstructor
public class BukkitEventEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Event event;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }
}
