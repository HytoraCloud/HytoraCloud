package de.lystx.cloudsystem.library.elements.packets.out.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCConfig;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.io.Serializable;


@Getter
public class PacketOutNPC extends Packet implements Serializable {

    private final NPCConfig npcConfig;
    private final VsonObject document;

    public PacketOutNPC(NPCConfig npcConfig, VsonObject document) {
        this.npcConfig = npcConfig;
        this.document = document;
    }

}
