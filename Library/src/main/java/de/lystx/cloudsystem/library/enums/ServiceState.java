package de.lystx.cloudsystem.library.enums;

import java.io.Serializable;

public enum ServiceState implements Serializable {


    FULL("§6"),
    MAINTENANCE("§b"),
    OFFLINE("§4"),
    INGAME("§c"),
    LOBBY("§a");


    private final String color;

    ServiceState(String color) {
        this.color = color;
    }


    public String getColor() {
        return color;
    }
}
