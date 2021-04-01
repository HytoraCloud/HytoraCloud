package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * This packet is used to handle
 * the log of a service
 * it will upload it to a pasteserver
 * and return the link of it
 */
@Getter @AllArgsConstructor
public class PacketInGetLog extends Packet implements Serializable {

    private final Service service;
    private final String player;

}
