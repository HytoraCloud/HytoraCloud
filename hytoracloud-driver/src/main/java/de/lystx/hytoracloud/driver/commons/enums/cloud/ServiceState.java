package de.lystx.hytoracloud.driver.commons.enums.cloud;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public enum ServiceState implements Serializable {

    /**
     * This service is full
     */
    FULL("§6"),

    /**
     * The group of it is in maintenance
     */
    MAINTENANCE("§b"),

    /**
     * The service is offline
     */
    OFFLINE("§4"),

    /**
     * The service is ingame
     */
    INGAME("§c"),

    /**
     * The service is in lobby phase
     */
    LOBBY("§a");

    private final String color;

}
