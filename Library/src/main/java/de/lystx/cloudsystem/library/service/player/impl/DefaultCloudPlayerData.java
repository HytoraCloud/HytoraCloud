package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import lombok.Getter;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;


@Getter
public class DefaultCloudPlayerData extends CloudPlayerData {

    public DefaultCloudPlayerData(UUID uuid, String name) {
        super(uuid, name, Collections.singletonList(new PermissionEntry(uuid, "Player", "")), new LinkedList<>(), "0", true, new Date().getTime(), 0L);
        this.setDefault(true);
    }
}
