package de.lystx.cloudsystem.library.elements.events.player;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import lombok.Getter;

@Getter
public class CloudPlayerPermissionGroupRemoveEvent extends Event {

    private final String playerName;
    private final PermissionGroup permissionGroup;

    public CloudPlayerPermissionGroupRemoveEvent(String playerName, PermissionGroup permissionGroup) {
        this.playerName = playerName;
        this.permissionGroup = permissionGroup;
    }
}
