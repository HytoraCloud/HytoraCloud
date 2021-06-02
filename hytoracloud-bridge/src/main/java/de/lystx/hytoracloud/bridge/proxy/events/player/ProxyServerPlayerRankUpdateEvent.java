package de.lystx.hytoracloud.bridge.proxy.events.player;

import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
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
