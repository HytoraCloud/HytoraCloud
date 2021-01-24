package de.lystx.cloudsystem.library.service.permission.impl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public class PermissionEntry implements Serializable {

    private UUID uuid;
    private String permissionGroup;
    private String validTime;

    public PermissionEntry(UUID uuid, String permissionGroup, String validTime) {
        this.uuid = uuid;
        this.permissionGroup = permissionGroup;
        this.validTime = validTime;
    }
}
