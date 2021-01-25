package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.Getter;

@Getter
public class PacketReceiverRegister extends PacketCommunication {

    private final String host;
    private final String name;

    public PacketReceiverRegister(String host, String name) {
        super(PacketReceiverRegister.class);
        this.host = host;
        this.name = name;
    }
}
