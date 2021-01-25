package de.lystx.cloudsystem.library.service.config.impl.fallback;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Fallback implements Serializable {

    private final int priority;
    private final String groupName;
    private final String permission;

    public Fallback(int priority, String groupName, String permission) {
        this.priority = priority;
        this.groupName = groupName;
        this.permission = permission;
    }
}
