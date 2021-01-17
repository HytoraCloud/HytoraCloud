package de.lystx.cloudsystem.library.elements.packets.out.service;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutExecuteCommand extends Packet implements Serializable {

    private final String service;
    private final String execution;

    public PacketPlayOutExecuteCommand(String service, String execution) {
        super();
        this.service = service;
        this.execution = execution;
    }
}
