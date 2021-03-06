package de.lystx.cloudsystem.library.service.permission.impl;

import java.io.Serializable;

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
