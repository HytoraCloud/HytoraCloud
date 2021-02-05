package de.lystx.cloudsystem.library.elements.packets.wrapper;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class WrapperPacketStartService extends Packet implements Serializable {

    private final Service service;
    private final SerializableDocument properties;

    public WrapperPacketStartService(Service service, SerializableDocument properties) {
        this.service = service;
        this.properties = properties;
    }
}
