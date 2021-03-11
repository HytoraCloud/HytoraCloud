package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInCopyTemplate extends Packet implements Serializable {

    private final Service service;
    private final String template;

    public PacketInCopyTemplate(Service service, String template) {
        this.service = service;
        this.template = template;
    }
}
