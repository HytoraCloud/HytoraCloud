package de.lystx.cloudsystem.library.service.player.impl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class CloudPlayerData implements Serializable {

    private UUID uuid;
    private String name;
    private String permissionGroup;
    private String tempPermissionGroup;
    private String validadilityTime;
    private List<String> permissions;
    private String ipAddress;
    private boolean notifyServerStart;
    private boolean isDefault;

    public CloudPlayerData(UUID uuid, String name, String permissionGroup, String tempPermissionGroup, String validadilityTime, List<String> permissions, String ipAddress, boolean notifyServerStart) {
        this.uuid = uuid;
        this.name = name;
        this.isDefault = false;
        this.permissionGroup = permissionGroup;
        this.tempPermissionGroup = tempPermissionGroup;
        this.validadilityTime = validadilityTime;
        this.permissions = permissions;
        this.ipAddress = ipAddress;
        this.notifyServerStart = notifyServerStart;
    }
}
