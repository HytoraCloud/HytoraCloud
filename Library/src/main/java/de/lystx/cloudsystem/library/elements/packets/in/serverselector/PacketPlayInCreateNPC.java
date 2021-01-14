package de.lystx.cloudsystem.library.elements.packets.in.serverselector;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.utils.Document;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInCreateNPC extends Packet implements Serializable {

    private final String key;
    private final String document;

    public PacketPlayInCreateNPC(String key, String document) {
        super(PacketPlayInCreateNPC.class);
        this.key = key;
        this.document = document;
    }

    public Document getDocument() {
        return new Document(this.document);
    }
}
