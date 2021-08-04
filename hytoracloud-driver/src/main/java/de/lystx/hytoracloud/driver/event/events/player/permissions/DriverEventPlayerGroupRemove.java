package de.lystx.hytoracloud.driver.event.events.player.permissions;

import de.lystx.hytoracloud.driver.event.IEvent;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class DriverEventPlayerGroupRemove implements IEvent, Serializable {

    private final String playerName;
    private final PermissionGroup permissionGroup;

}
