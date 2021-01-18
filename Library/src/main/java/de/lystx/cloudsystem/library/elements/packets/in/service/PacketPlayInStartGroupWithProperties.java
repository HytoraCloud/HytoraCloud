package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInStartGroupWithProperties extends Packet implements Serializable {

    private final ServiceGroup serviceGroup;
    private final String properties;

    public PacketPlayInStartGroupWithProperties(ServiceGroup serviceGroup, Document properties) {
        this.serviceGroup = serviceGroup;
        this.properties = properties.toString();
    }

    public Document getProperties() {
        return new Document(this.properties);
    }
}
