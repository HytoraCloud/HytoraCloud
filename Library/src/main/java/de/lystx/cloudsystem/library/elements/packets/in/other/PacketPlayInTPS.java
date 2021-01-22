package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInTPS extends Packet implements Serializable {

    private final String playerName;

    public PacketPlayInTPS(String playerName) {
        this.playerName = playerName;
    }
}
