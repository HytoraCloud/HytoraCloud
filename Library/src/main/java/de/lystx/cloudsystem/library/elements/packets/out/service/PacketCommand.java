package de.lystx.cloudsystem.library.elements.packets.out.service;

import de.lystx.cloudsystem.library.elements.packets.both.PacketCommunication;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketCommand extends PacketCommunication implements Serializable {

    public PacketCommand(String service, String execution) {
        this.append("key", "executeCommand");
        this.append("service", service);
        this.append("command", execution);
    }


}
