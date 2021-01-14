package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PacketCommunicationSendTitle extends PacketCommunication {

    private final String name;
    private final String title;
    private final String subtitle;

    public PacketCommunicationSendTitle(String name, String title, String subtitle) {
        super(PacketCommunicationSendTitle.class);
        this.name = name;
        this.title = title;
        this.subtitle = subtitle;
    }
}
