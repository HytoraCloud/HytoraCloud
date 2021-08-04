package de.lystx.hytoracloud.driver.utils.interfaces;

import de.lystx.hytoracloud.driver.wrapped.IdentifiableObject;

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
    Identifiable ALL = new IdentifiableObject("ALL", UUID.randomUUID());

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
