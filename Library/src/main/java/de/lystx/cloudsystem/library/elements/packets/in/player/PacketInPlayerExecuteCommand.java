package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInPlayerExecuteCommand extends Packet implements Serializable {

    private final String player;
    private final String command;

    public PacketInPlayerExecuteCommand(String player, String command) {
        this.player = player;
        this.command = command;
    }
}
