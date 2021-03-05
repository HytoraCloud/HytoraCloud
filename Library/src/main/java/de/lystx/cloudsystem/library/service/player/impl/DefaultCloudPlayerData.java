package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;


@Getter
public class DefaultCloudPlayerData extends CloudPlayerData {

    /**
     * Creates default data
     * @param uuid
     * @param name
     */
    public DefaultCloudPlayerData(UUID uuid, String name, String ip) {
        super(uuid, name, Collections.singletonList(new PermissionEntry(uuid, Constants.PERMISSION_POOL.getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), ip, true, new Date().getTime(), 0L);
        this.setDefault(true);
    }

}
