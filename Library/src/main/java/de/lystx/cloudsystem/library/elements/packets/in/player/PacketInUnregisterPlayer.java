package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.elements.packets.both.PacketCommunication;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInUnregisterPlayer extends PacketCommunication implements Serializable {

    private final String name;

}
