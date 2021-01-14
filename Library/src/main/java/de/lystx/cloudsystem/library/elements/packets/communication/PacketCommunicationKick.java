package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.Getter;


@Getter
public class PacketCommunicationKick extends PacketCommunication{

    private final String name;
    private final String reason;

    public PacketCommunicationKick(String name, String reason) {
        super(PacketCommunicationKick.class);
        this.name = name;
        this.reason = reason;
    }
}
