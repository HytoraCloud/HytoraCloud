package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInUpdateServiceGroup extends Packet implements Serializable {

    private final ServiceGroup serviceGroup;

    public PacketPlayInUpdateServiceGroup(ServiceGroup serviceGroup) {
        super(PacketPlayInUpdateServiceGroup.class);
        this.serviceGroup = serviceGroup;
    }
}
