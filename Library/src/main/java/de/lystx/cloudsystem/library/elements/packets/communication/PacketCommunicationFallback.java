package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketCommunicationFallback extends PacketCommunication implements Serializable {

    private final String name;

    public PacketCommunicationFallback(String name) {
        super(PacketCommunicationFallback.class);
        this.name = name;
    }
}
