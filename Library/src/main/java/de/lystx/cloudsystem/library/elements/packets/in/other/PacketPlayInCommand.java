package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInCommand extends Packet implements Serializable {

    private final String command;

    public PacketPlayInCommand(String command) {
        this.command = command;
    }
}
