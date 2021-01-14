package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInRegister extends Packet implements Serializable {

    private final Service service;

    public PacketPlayInRegister(Service service) {
        super(PacketPlayInRegister.class);
        this.service = service;
    }
}
