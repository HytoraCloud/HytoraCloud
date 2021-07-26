package de.lystx.hytoracloud.driver.commons.enums.cloud;

import java.io.Serializable;

public enum ServiceType implements Serializable {

    SPIGOT,
    NONE,
    CLOUDSYSTEM,
    PROXY;

    /**
     * Checks if the current type is proxy
     *
     * @return boolean
     */
    public boolean isProxy() {
        return this == PROXY;
    }
}
