package de.lystx.hytoracloud.driver.commons.enums.cloud;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public enum ServiceState implements Serializable {


    FULL("§6"),
    MAINTENANCE("§b"),
    OFFLINE("§4"),
    INGAME("§c"),
    LOBBY("§a");

    private final String color;

}
