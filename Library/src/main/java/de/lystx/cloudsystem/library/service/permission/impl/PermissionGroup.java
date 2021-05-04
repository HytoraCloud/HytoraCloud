package de.lystx.cloudsystem.library.service.permission.impl;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.Cloud;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
public class PermissionGroup implements Serializable {

    private String name;
    private Integer id;
    private String prefix;
    private String suffix;
    private String display;
    private String chatFormat;
    private List<String> permissions;
    private List<String> inheritances;
    private SerializableDocument entries;

    public PermissionGroup(String name, Integer id, String prefix, String suffix, String display, String chatFormat, List<String> permissions, List<String> inheritances) {
        this(name, id, prefix, suffix, display, chatFormat, permissions, inheritances, new SerializableDocument());
    }

    public PermissionGroup(String name, Integer id, String prefix, String suffix, String display, String chatFormat, List<String> permissions, List<String> inheritances, SerializableDocument entries) {
        this.name = name;
        this.id = id;
        this.prefix = prefix;
        this.suffix = suffix;
        this.display = display;
        this.chatFormat = chatFormat;
        this.permissions = permissions;
        this.inheritances = inheritances;
        this.entries = entries;
    }

    /**
     * Updates the current group
     * Not safe because {@link Cloud#PERMISSION_POOL} might
     * or could be null at any time when it's not set
     */
    public void update() {
        //TODO: SAFELY UPDATE AND SEND TO CLOUD
        PermissionGroup group = Cloud.getInstance().getPermissionPool().getPermissionGroupFromName(this.name);
        Cloud.getInstance().getPermissionPool().getPermissionGroups().set(Cloud.getInstance().getPermissionPool().getPermissionGroups().indexOf(group), this);
        Cloud.getInstance().getPermissionPool().update();
    }
}
