package de.lystx.cloudsystem.library.elements.events.player;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionValidality;
import lombok.Getter;

@Getter
public class CloudPlayerPermissionGroupAddEvent extends Event {

    private final String name;
    private final PermissionGroup permissionGroup;
    private final int duration;
    private final PermissionValidality validality;

    public CloudPlayerPermissionGroupAddEvent(String name, PermissionGroup permissionGroup, int duration, PermissionValidality validality) {
        this.name = name;
        this.permissionGroup = permissionGroup;
        this.duration = duration;
        this.validality = validality;
    }
}
