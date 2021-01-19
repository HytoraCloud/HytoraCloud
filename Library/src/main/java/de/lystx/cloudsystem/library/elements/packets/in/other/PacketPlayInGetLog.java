package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInGetLog extends Packet implements Serializable {

    private final Service service;
    private final String player;

    public PacketPlayInGetLog(Service service, String player) {
        this.service = service;
        this.player = player;
    }
}
