package de.lystx.cloudsystem.library.elements.packets.out.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class PacketOutRegisterServer extends Packet implements Serializable {

    private final Service service;
    private String action;


    public PacketOutRegisterServer(Service service) {
        this.service = service;
    }

    public PacketOutRegisterServer setAction(String action) {
        this.action = action;
        return this;
    }
}
