package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInStartGroupWithProperties extends Packet implements Serializable {

    private final ServiceGroup serviceGroup;
    private final VsonObject properties;

    public PacketPlayInStartGroupWithProperties(ServiceGroup serviceGroup, VsonObject properties) {
        this.serviceGroup = serviceGroup;
        this.properties = properties;
    }

}
