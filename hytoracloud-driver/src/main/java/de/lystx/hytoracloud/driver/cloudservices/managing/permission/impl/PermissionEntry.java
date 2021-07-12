package de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

/**
 * The PermissionEntry contains
 * the {@link UUID} of the {@link CloudPlayer}
 * the Entry belongs to,
 * the name of the {@link PermissionGroup},
 * and the validality Time (means when the rank expires)
 */
@Getter @Setter @AllArgsConstructor @ToString
public class PermissionEntry implements Serializable {

    private String permissionGroup;
    private String validTime;

}
