package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.Getter;

@Getter
public class PacketCommunicationPlayerChat extends PacketCommunication{

    private final String player;
    private final String message;

    public PacketCommunicationPlayerChat(String player, String message) {
        super(PacketCommunicationPlayerChat.class);
        this.player = player;
        this.message = message;
    }
}
