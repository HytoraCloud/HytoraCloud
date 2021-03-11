package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.packets.both.PacketCommunication;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInUpdateServiceGroup extends PacketCommunication implements Serializable {

    private final ServiceGroup serviceGroup;

    public PacketInUpdateServiceGroup(ServiceGroup serviceGroup) {
        this.serviceGroup = serviceGroup;
    }
}
