package de.lystx.cloudsystem.library.elements.packets.out.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
public class PacketPlayOutServices extends Packet implements Serializable {

    private final Map<ServiceGroup, List<Service>> services;

    public PacketPlayOutServices(Map<ServiceGroup, List<Service>> services) {
        this.services = services;
    }
}
