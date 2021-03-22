package de.lystx.cloudsystem.library.service.network.connection.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum PacketState {

    SUCCESS("§a"), //The packet was sent succesfully
    FAILED("§c"), //The packet couldn't be send
    NULL("§4"), //Something was null
    RETRY("§6"); //The packet will be send another 5 times


    private final String color;

    @Override
    public String toString() {
        return color + name();
    }
}
