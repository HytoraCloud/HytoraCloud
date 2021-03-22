package de.lystx.cloudsystem.library.elements.packets.out.other;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketOutReceiverServerBootedUp extends Packet {

    private final Service service;
    private final String action;
}
