package de.lystx.hytoracloud.driver.commons.interfaces;

import java.io.Serializable;
import java.util.UUID;

/**
 * This class is used to identify
 * given objects
 */
public interface Identifiable extends Serializable {

    static Identifiable ALL = new Identifiable() {

        private static final long serialVersionUID = -7885939793418108538L;

        @Override
        public String getName() {
            return "ALL";
        }

        @Override
        public UUID getUniqueId() {
            return UUID.randomUUID();
        }

        @Override
        public void setUniqueId(UUID uniqueId) {
        }

        @Override
        public void setName(String name) {
        }
    };

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
