package de.lystx.cloudsystem.library.elements.packets.out.service;

import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCommunication;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketCommand extends PacketCommunication implements Serializable {

    public PacketCommand(String service, String execution) {
        this.put("key", "executeCommand");
        this.put("service", service);
        this.put("command", execution);
    }


}
