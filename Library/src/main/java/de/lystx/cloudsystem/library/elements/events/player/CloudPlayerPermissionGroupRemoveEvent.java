package de.lystx.cloudsystem.library.elements.events.player;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CloudPlayerPermissionGroupRemoveEvent extends Event {

    private final String playerName;
    private final PermissionGroup permissionGroup;

}
