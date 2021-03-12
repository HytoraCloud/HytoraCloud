package de.lystx.cloudsystem.library.elements.packets.out.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketOutStopServer extends Packet implements Serializable {

    private final Service service;

    public PacketOutStopServer(Service service) {
        this.service = service;
    }

}
