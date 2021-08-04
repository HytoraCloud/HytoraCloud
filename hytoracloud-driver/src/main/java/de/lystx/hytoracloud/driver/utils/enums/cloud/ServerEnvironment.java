package de.lystx.hytoracloud.driver.utils.enums.cloud;

import java.io.Serializable;

public enum ServerEnvironment implements Serializable {

    /**
     * This is a spigot instance
     */
    SPIGOT,

    /**
     * Is not defined
     */
    NONE,

    /**
     * This is a cloud instance
     */
    CLOUD,

    /**
     * This is a proxy instance
     */
    PROXY;

}
