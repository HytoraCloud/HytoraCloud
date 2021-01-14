package de.lystx.cloudsystem.library.service.config.impl.fallback;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Fallback implements Serializable {

    private final String groupName;
    private final String permission;

    public Fallback(String groupName, String permission) {
        this.groupName = groupName;
        this.permission = permission;
    }
}
