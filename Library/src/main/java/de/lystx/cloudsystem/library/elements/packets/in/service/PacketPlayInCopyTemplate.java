package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInRemoveNPC;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInCopyTemplate extends Packet implements Serializable {

    private final Service service;
    private final String template;

    public PacketPlayInCopyTemplate(Service service, String template) {
        super(PacketPlayInCopyTemplate.class);
        this.service = service;
        this.template = template;
    }
}
