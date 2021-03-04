package de.lystx.cloudsystem.library.service.permission.impl;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;

import java.util.LinkedList;

public class DefaultPermissionGroup extends PermissionGroup {

    /**
     * Creates default group
     */
    public DefaultPermissionGroup() {
        super("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new SerializableDocument());
    }
}
