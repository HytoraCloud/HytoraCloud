package de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base;



import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Serializable Class for the
 * CloudSign to work with it later
 */
@Getter @AllArgsConstructor
public class CloudSign implements Serializable, Objectable<CloudSign> {

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
        this(UUID.randomUUID(), x, y, z, group, world);
    }

    public CloudSign() {
        this(0, 0, 0, "", "");
    }


}
