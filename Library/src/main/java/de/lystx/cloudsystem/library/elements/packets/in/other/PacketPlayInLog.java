package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInLog extends Packet implements Serializable {

    private final String prefix;
    private final String message;
    private final boolean showUpInConsole;

    public PacketPlayInLog(String prefix, String message, boolean showUpInConsole) {
        super();
        this.prefix = prefix;
        this.message = message;
        this.showUpInConsole = showUpInConsole;
    }
}
