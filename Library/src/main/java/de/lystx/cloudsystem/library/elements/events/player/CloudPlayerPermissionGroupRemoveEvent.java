package de.lystx.cloudsystem.library.elements.events.player;

import de.lystx.cloudsystem.library.service.event.Event;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class CloudPlayerPermissionGroupRemoveEvent extends Event implements Serializable {

    private final String playerName;
    private final PermissionGroup permissionGroup;

}
