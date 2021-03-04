package de.lystx.cloudsystem.library.elements.events.player;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionValidality;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CloudPlayerPermissionGroupAddEvent extends Event {

    private final String name;
    private final PermissionGroup permissionGroup;
    private final int duration;
    private final PermissionValidality validality;

}
