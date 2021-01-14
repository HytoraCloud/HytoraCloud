package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunication;
import de.lystx.cloudsystem.library.elements.service.Service;
import lombok.Getter;

@Getter
public class PacketPlayInServiceUpdate extends PacketCommunication {

    private final Service service;

    public PacketPlayInServiceUpdate(Service service) {
        super(PacketPlayInServiceUpdate.class);
        this.service = service;
    }
}
