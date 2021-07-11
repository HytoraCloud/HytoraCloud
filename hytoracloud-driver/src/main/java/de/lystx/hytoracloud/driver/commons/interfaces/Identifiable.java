package de.lystx.hytoracloud.driver.commons.interfaces;

import java.util.UUID;

/**
 * This class is used to identify
 * given objects
 */
public interface Identifiable {

    /**
     * Gets the name of this object
     *
     * @return name of object
     */
    String getName();

    /**
     * Gets the UUID of this object
     *
     * @return uuid of object
     */
    UUID getUniqueId();

    /**
     * Sets the uuid of this object
     *
     * @param uniqueId the uuid
     */
    void setUniqueId(UUID uniqueId);

    /**
     * Sets the name of this object
     *
     * @param name the name
     */
    void setName(String name);
}
