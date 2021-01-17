package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInUnregisterCloudPlayer extends Packet implements Serializable {

    private final String name;

    public PacketPlayInUnregisterCloudPlayer(String name) {
        super();
        this.name = name;
    }
}
