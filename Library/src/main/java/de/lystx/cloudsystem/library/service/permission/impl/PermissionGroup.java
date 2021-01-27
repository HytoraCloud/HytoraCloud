package de.lystx.cloudsystem.library.service.permission.impl;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class PermissionGroup implements Serializable {

    private final String name;
    private final Integer id;
    private final String prefix;
    private final String suffix;
    private final String display;
    private final String chatFormat;
    private final List<String> permissions;
    private final List<String> inheritances;

    public PermissionGroup(String name, Integer id, String prefix, String suffix, String display, String chatFormat, List<String> permissions, List<String> inheritances) {
        this.name = name;
        this.id = id;
        this.prefix = prefix;
        this.suffix = suffix;
        this.display = display;
        this.chatFormat = chatFormat;
        this.permissions = permissions;
        this.inheritances = inheritances;
    }
}
