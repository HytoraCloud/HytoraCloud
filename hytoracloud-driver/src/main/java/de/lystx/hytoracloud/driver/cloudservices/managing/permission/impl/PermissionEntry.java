package de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

/**
 * The PermissionEntry contains
 * the {@link UUID} of the {@link ICloudPlayer}
 * the Entry belongs to,
 * the name of the {@link PermissionGroup},
 * and the validality Time (means when the rank expires)
 */
@Getter @Setter @AllArgsConstructor @ToString
public class PermissionEntry implements Serializable {

    private static final long serialVersionUID = 7716950517479203380L;

    /**
     * The given group
     */
    private String permissionGroup;

    /**
     * The time it expires
     */
    private String validTime;

}
