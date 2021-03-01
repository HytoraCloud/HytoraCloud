package de.lystx.cloudapi.bukkit.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor @Getter
public class CloudPlayerLabyModJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String version;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }
}
