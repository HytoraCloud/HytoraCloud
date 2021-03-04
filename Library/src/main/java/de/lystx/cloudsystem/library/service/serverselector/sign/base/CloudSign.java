package de.lystx.cloudsystem.library.service.serverselector.sign.base;


import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class CloudSign implements Serializable {

    private final UUID uuid;
    private final Integer x;
    private final Integer y;
    private final Integer z;
    private final String group;
    private final String world;

    /**
     * Constructs a CloudSign
     * @param x
     * @param y
     * @param z
     * @param group
     * @param world
     */
    public CloudSign(Integer x, Integer y, Integer z, String group, String world) {
        this.x = x;
        this.uuid = UUID.randomUUID();
        this.y = y;
        this.z = z;
        this.group = group;
        this.world = world;
    }
}
