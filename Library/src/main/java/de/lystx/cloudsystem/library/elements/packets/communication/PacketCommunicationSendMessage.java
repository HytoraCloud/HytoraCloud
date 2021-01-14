package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PacketCommunicationSendMessage extends PacketCommunication {

    private final UUID uuid;
    private final String message;

    public PacketCommunicationSendMessage(UUID uuid, String message) {
        super(PacketCommunicationSendMessage.class);
        this.uuid = uuid;
        this.message = message;
    }
}
