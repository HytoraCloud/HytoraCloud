package de.lystx.cloudsystem.library.service.permission.impl;

import java.util.LinkedList;

public class DefaultPermissionGroup extends PermissionGroup{

    public DefaultPermissionGroup() {
        super("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>());
    }
}
