package de.lystx.cloudsystem.library.elements.packets.in.serverselector;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInCreateNPC extends Packet implements Serializable {

    private final String key;
    private final String doc;

    public PacketPlayInCreateNPC(String key, String doc) {
        super();
        this.key = key;
        this.doc = doc;
    }

    public Document getDocument() {
        return new Document(this.doc);
    }
}
