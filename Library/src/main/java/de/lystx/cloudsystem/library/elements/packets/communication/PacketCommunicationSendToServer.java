package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PacketCommunicationSendToServer extends PacketCommunication {

    private final String name;
    private final String server;

    public PacketCommunicationSendToServer(String name, String server) {
        super(PacketCommunicationSendToServer.class);
        this.name = name;
        this.server = server;
    }
}
