package de.lystx.cloudapi.bukkit.events.player;

import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This Event is called when a CloudPlayer
 * Gets a (new) rank added or removed
 */
@AllArgsConstructor @Getter
public class CloudServerPlayerRankUpdateEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final CloudPlayer cloudPlayer;
    private final PermissionGroup permissionGroup;
    private final boolean add;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
