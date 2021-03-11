package de.lystx.cloudsystem.library.elements.packets.in.serverselector;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInDeleteNPC extends Packet implements Serializable {

    private final String key;

    public PacketInDeleteNPC(String key) {
        this.key = key;
    }

}
