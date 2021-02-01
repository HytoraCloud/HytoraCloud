package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public CloudPlayerData(UUID uuid, String name, List<PermissionEntry> permissionEntries, List<String> permissions, String ipAddress, boolean notifyServerStart, long firstLogin, long lastLogin) {
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

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public PermissionEntry getForGroup(String group) {
        for (PermissionEntry permissionEntry : this.permissionEntries) {
            if (group.equalsIgnoreCase(permissionEntry.getPermissionGroup())) {
                return permissionEntry;
            }
        }
        return null;
    }

    public Map<String, Object> getAsMap() {
        Map<String, Object> map = new HashMap<>();
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            try {
                map.put(declaredField.getName(), declaredField.get(this));
            } catch (IllegalAccessException e) {}
        }
        return map;
    }

}
