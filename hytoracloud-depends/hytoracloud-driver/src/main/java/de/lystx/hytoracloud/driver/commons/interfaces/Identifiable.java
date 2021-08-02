package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.commons.wrapped.Identification;

import java.io.Serializable;
import java.util.UUID;

/**
 * This class is used to identify
 * given objects
 */
public interface Identifiable extends Serializable {


    /**
     * An {@link Identifiable} object for the parameter "ALL"
     */
    Identifiable ALL = new Identification("ALL", UUID.randomUUID());

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

}
