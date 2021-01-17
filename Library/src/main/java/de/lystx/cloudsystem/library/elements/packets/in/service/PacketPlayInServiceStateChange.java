package de.lystx.cloudsystem.library.elements.packets.in.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInServiceStateChange extends Packet implements Serializable {

    private final Service service;
    private final ServiceState serviceState;

    public PacketPlayInServiceStateChange(Service service, ServiceState serviceState) {
        super();
        this.service = service;
        this.serviceState = serviceState;
    }
}
