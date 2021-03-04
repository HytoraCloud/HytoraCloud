package de.lystx.cloudsystem.library.service.permission.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class PermissionEntry implements Serializable {

    private UUID uuid;
    private String permissionGroup;
    private String validTime;

}
