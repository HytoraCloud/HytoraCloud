package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInUnregisterPlayer extends Packet implements Serializable {

    private final String name;

    public PacketInUnregisterPlayer(String name) {
        this.name = name;
    }
}
