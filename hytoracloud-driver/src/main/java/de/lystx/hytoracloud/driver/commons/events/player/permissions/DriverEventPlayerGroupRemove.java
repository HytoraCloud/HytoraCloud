package de.lystx.hytoracloud.driver.commons.events.player.permissions;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventPlayerGroupRemove extends CloudEvent implements Serializable {

    private final String playerName;
    private final PermissionGroup permissionGroup;

}
