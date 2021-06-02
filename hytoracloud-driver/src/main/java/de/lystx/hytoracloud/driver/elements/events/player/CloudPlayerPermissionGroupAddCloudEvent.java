package de.lystx.hytoracloud.driver.elements.events.player;

import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionValidity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class CloudPlayerPermissionGroupAddCloudEvent extends CloudEvent implements Serializable {

    private final String name;
    private final PermissionGroup permissionGroup;
    private final int duration;
    private final PermissionValidity validality;

}
