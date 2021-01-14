package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.Getter;

@Getter
public class PacketCommunicationFallback extends PacketCommunication{

    private final String name;

    public PacketCommunicationFallback(String name) {
        super(PacketCommunicationFallback.class);
        this.name = name;
    }
}
