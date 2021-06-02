package de.lystx.hytoracloud.bridge.bukkit.events.player;

import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor @Getter
public class CloudPlayerLabyModJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final CloudPlayer player;
    private final String version;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }
}
