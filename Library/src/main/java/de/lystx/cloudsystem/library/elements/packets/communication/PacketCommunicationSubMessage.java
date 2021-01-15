package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import lombok.Getter;

@Getter
public class PacketCommunicationSubMessage extends PacketCommunication {

    private final String channel;
    private final String key;
    private final String document;
    private final ServiceType type;

    public PacketCommunicationSubMessage(String channel, String key, String document, ServiceType type) {
        super(PacketCommunicationSubMessage.class);
        this.channel = channel;
        this.key = key;
        this.document = document;
        this.type = type;
    }

    public Document getDocument() {
        return new Document(this.document);
    }
}
