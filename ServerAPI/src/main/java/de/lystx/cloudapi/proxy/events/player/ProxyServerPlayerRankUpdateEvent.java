package de.lystx.cloudapi.proxy.events.player;

import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

/**
 * This Event is called when a CloudPlayer
 * Gets a (new) rank added or removed
 */
@AllArgsConstructor @Getter
public class ProxyServerPlayerRankUpdateEvent extends Event implements Objectable {

    private final CloudPlayer cloudPlayer;
    private final PermissionGroup permissionGroup;
    private final boolean add;
}
