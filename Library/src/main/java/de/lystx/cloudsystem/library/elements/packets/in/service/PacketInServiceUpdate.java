package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.packets.both.PacketCommunication;
import de.lystx.cloudsystem.library.elements.service.Service;
import lombok.Getter;

@Getter
public class PacketInServiceUpdate extends PacketCommunication {

    private final Service service;

    public PacketInServiceUpdate(Service service) {
        this.service = service;
    }
}
