package de.lystx.cloudsystem.library.elements.packets.out;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOutVerifyConnection extends Packet implements Serializable {

    private final String ip;
    private final int port;
}
