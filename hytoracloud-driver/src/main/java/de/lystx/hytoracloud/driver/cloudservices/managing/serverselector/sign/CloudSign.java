package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign;



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

    private static final long serialVersionUID = -2627011017230396868L;

    /**
     * The uuid of this sign
     */
    private final UUID uuid;

    /**
     * the x location
     */
    private final Integer x;

    /**
     * The y location
     */
    private final Integer y;

    /**
     * the z location
     */
    private final Integer z;

    /**
     * The group for it
     */
    private final String group;

    /**
     * The world of the location
     */
    private final String world;

    public CloudSign(Integer x, Integer y, Integer z, String group, String world) {
        this(UUID.randomUUID(), x, y, z, group, world);
    }

    public CloudSign() {
        this(0, 0, 0, "", "");
    }


}
