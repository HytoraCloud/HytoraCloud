package de.lystx.cloudsystem.library.service.permission.impl;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

import java.io.Serializable;

/**
 * Used to define how long a {@link PermissionEntry}
 * will last until it expires and will be removed
 * from the given {@link CloudPlayer}
 */
public enum PermissionValidality implements Serializable {

    SECOND,
    MINUTE,
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR,
    LIFETIME
}
