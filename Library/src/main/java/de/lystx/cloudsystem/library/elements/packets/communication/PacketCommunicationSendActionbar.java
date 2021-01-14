package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PacketCommunicationSendActionbar extends PacketCommunication {

    private final String name;
    private final String message;

    public PacketCommunicationSendActionbar(String name, String message) {
        super(PacketCommunicationSendActionbar.class);
        this.name = name;
        this.message = message;
    }
}
