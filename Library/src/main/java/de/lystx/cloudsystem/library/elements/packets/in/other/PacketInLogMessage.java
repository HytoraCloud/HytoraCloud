package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInLogMessage extends Packet implements Serializable {

    private final String prefix;
    private final String message;
    private final boolean showUpInConsole;

    public PacketInLogMessage(String prefix, String message, boolean showUpInConsole) {
        this.prefix = prefix;
        this.message = message;
        this.showUpInConsole = showUpInConsole;
    }
}
