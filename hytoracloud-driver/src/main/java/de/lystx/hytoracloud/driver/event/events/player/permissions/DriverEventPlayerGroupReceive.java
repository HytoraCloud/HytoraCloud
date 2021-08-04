package de.lystx.hytoracloud.driver.event.events.player.permissions;

import de.lystx.hytoracloud.driver.event.IEvent;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionValidity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventPlayerGroupReceive implements IEvent, Serializable {

    private static final long serialVersionUID = -1107020116701439608L;
    private final String name;
    private final PermissionGroup permissionGroup;
    private final int duration;
    private final PermissionValidity validality;

}
