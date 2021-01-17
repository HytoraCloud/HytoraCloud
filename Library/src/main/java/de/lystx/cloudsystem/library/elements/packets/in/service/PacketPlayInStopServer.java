package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

import java.io.Serializable;


public class PacketPlayInStopServer extends Packet implements Serializable {

    private final Service service;

    public PacketPlayInStopServer(Service service) {
        super();
        this.service = service;
    }

    public Service getService() {
        return service;
    }
}
