package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class PacketInRegister extends Packet implements Serializable {

    private final Service service;
    private String action;

    public PacketInRegister(Service service) {
        this.service = service;
    }
}
