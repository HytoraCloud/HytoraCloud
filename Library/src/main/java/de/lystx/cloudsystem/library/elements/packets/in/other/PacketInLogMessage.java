package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * This packet is used to send
 * a message to the CloudSystem
 *
 * and you can choose if you only
 * want it to show up in the log or
 * in the console if you change showUpInConsole
 * to false
 */
@Getter @AllArgsConstructor
public class PacketInLogMessage extends Packet implements Serializable {

    private final String prefix;
    private final String message;
    private final boolean showUpInConsole;

}
