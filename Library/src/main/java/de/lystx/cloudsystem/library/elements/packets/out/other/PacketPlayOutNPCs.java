package de.lystx.cloudsystem.library.elements.packets.out.other;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCConfig;

import java.io.Serializable;


public class PacketPlayOutNPCs extends Packet implements Serializable {

    private final NPCConfig npcConfig;
    private final String document;

    public PacketPlayOutNPCs(NPCConfig npcConfig, Document document) {
        this.npcConfig = npcConfig;
        this.document = document.toString();
    }

    public NPCConfig getNpcConfig() {
        return npcConfig;
    }

    public Document getDocument() {
        return new Document(this.document);
    }
}
