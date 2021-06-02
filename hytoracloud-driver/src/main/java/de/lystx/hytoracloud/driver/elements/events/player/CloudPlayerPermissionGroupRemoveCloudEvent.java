package de.lystx.hytoracloud.driver.elements.events.player;

import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class CloudPlayerPermissionGroupRemoveCloudEvent extends CloudEvent implements Serializable {

    private final String playerName;
    private final PermissionGroup permissionGroup;

}
