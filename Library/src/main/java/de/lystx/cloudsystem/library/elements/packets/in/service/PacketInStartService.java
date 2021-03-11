package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.elements.other.Document;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInStartService extends Packet implements Serializable {

    private final Service service;
    private final SerializableDocument properties;

    public PacketInStartService(Service service, SerializableDocument properties) {
        this.service = service;
        this.properties = properties;
    }

}
