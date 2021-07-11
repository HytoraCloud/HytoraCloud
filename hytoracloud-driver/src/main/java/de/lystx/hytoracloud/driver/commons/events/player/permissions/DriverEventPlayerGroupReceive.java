package de.lystx.hytoracloud.driver.commons.events.player.permissions;

import de.lystx.hytoracloud.driver.service.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.service.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.managing.permission.impl.PermissionValidity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventPlayerGroupReceive extends CloudEvent implements Serializable {

    private final String name;
    private final PermissionGroup permissionGroup;
    private final int duration;
    private final PermissionValidity validality;

}
