package de.lystx.cloudsystem.library.elements.packets.out.service;

import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutUpdateServiceGroup extends Packet implements Serializable {

    private final ServiceGroup serviceGroup;

    public PacketPlayOutUpdateServiceGroup(ServiceGroup serviceGroup) {
        super();
        this.serviceGroup = serviceGroup;
    }
}
