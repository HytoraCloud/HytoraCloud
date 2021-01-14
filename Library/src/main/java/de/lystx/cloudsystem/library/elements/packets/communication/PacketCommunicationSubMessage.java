package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.utils.Document;
import lombok.Getter;

@Getter
public class PacketCommunicationSubMessage extends PacketCommunication {

    private final String channel;
    private final String key;
    private final String document;

    public PacketCommunicationSubMessage(String channel, String key, String document) {
        super(PacketCommunicationSubMessage.class);
        this.channel = channel;
        this.key = key;
        this.document = document;
    }

    public Document getDocument() {
        return new Document(this.document);
    }
}
