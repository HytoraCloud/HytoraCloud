package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.elements.other.Document;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInStartService extends Packet implements Serializable {

    private final Service service;
    private final VsonObject properties;

    public PacketPlayInStartService(Service service, VsonObject properties) {
        super();
        this.service = service;
        this.properties = properties;
    }

}
