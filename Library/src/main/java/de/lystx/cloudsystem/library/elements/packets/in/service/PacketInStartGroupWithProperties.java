package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInStartGroupWithProperties extends Packet implements Serializable {

    private final ServiceGroup serviceGroup;
    private final SerializableDocument properties;

    public PacketInStartGroupWithProperties(ServiceGroup serviceGroup, SerializableDocument properties) {
        this.serviceGroup = serviceGroup;
        this.properties = properties;
    }

}
