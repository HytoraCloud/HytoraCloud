package de.lystx.cloudsystem.library.elements.packets.both.other;

import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;

@Getter
public class PacketDocument extends PacketCommunication {

    private final String key;

    public PacketDocument(String key, Document document) {
        this.key = key;
    }
}
