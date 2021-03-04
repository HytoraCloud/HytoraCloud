package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class CloudPlayerData implements Serializable {

    private UUID uuid;
    private String name;
    private List<PermissionEntry> permissionEntries;
    private List<String> permissions;
    private String ipAddress;
    private boolean notifyServerStart;
    private boolean isDefault;
    private long firstLogin;
    private long lastLogin;

    public CloudPlayerData(
            UUID uuid,
            String name,
            List<PermissionEntry> permissionEntries,
            List<String> permissions,
            String ipAddress,
            boolean notifyServerStart,
            long firstLogin,
            long lastLogin) {
        this.uuid = uuid;
        this.name = name;
        this.firstLogin = firstLogin;
        this.lastLogin = lastLogin;
        this.isDefault = false;
        this.permissionEntries = permissionEntries;
        this.permissions = permissions;
        this.ipAddress = ipAddress;
        this.notifyServerStart = notifyServerStart;
    }

    /**
     * Gets Entry
     * @param group
     * @return PermissionEntry from group (e.g. "Admin")
     */
    public PermissionEntry getForGroup(String group) {
        return this.permissionEntries.stream().filter(permissionEntry -> group.equalsIgnoreCase(permissionEntry.getPermissionGroup())).findFirst().orElse(null);
    }

}
