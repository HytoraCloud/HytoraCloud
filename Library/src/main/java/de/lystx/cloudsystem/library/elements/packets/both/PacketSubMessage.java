package de.lystx.cloudsystem.library.elements.packets.both;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketSubMessage extends PacketCommunication {

    private final String channel;
    private final String key;
    private final String document;
    private final ServiceType type;

    public Document getDocument() {
        return new Document(this.document);
    }
}
