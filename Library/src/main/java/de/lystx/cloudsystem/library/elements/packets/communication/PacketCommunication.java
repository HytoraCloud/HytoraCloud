package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

import java.io.Serializable;

public class PacketCommunication extends Packet implements Serializable {

    public PacketCommunication(Class<?> clazz) {
        super(clazz);
    }
}
