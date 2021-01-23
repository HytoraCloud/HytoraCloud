package de.lystx.cloudsystem.library.service.player.impl;

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
    private String permissionGroup;
    private String tempPermissionGroup;
    private String validadilityTime;
    private List<String> permissions;
    private String ipAddress;
    private boolean notifyServerStart;
    private boolean isDefault;
    private long firstLogin;
    private long lastLogin;

    public CloudPlayerData(UUID uuid, String name, String permissionGroup, String tempPermissionGroup, String validadilityTime, List<String> permissions, String ipAddress, boolean notifyServerStart, long firstLogin, long lastLogin) {
        this.uuid = uuid;
        this.name = name;
        this.firstLogin = firstLogin;
        this.lastLogin = lastLogin;
        this.isDefault = false;
        this.permissionGroup = permissionGroup;
        this.tempPermissionGroup = tempPermissionGroup;
        this.validadilityTime = validadilityTime;
        this.permissions = permissions;
        this.ipAddress = ipAddress;
        this.notifyServerStart = notifyServerStart;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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
