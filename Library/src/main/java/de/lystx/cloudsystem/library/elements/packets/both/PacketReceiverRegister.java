package de.lystx.cloudsystem.library.elements.packets.both;

import lombok.Getter;

@Getter
public class PacketReceiverRegister extends PacketCommunication {

    private final String host;
    private final String name;

    public PacketReceiverRegister(String host, String name) {
        this.host = host;
        this.name = name;
    }
}
