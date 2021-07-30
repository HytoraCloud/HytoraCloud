package de.lystx.hytoracloud.driver.commons.events.player.permissions;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionValidity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventPlayerGroupReceive extends CloudEvent implements Serializable {

    private static final long serialVersionUID = -1107020116701439608L;
    private final String name;
    private final PermissionGroup permissionGroup;
    private final int duration;
    private final PermissionValidity validality;

}
