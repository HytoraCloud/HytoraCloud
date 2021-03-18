package de.lystx.cloudsystem.library.elements.events.player;

import de.lystx.cloudsystem.library.service.event.Event;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionValidality;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class CloudPlayerPermissionGroupAddEvent extends Event implements Serializable {

    private final String name;
    private final PermissionGroup permissionGroup;
    private final int duration;
    private final PermissionValidality validality;

}
