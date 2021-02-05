package de.lystx.cloudsystem.library.elements.packets.out.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class PacketPlayOutRegisterServer extends Packet implements Serializable {

    private final Service service;
    private String action;

    public PacketPlayOutRegisterServer(Service service) {
        super();
        this.service = service;
    }

    public PacketPlayOutRegisterServer setAction(String action) {
        this.action = action;
        return this;
    }
}
