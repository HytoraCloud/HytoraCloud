package de.lystx.cloudsystem.library.elements.packets.out.service;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutExecuteCommand extends Packet implements Serializable {

    public PacketPlayOutExecuteCommand(String service, String execution) {
        this.append("key", "executeCommand");
        this.append("service", service);
        this.append("command", execution);
    }
}
