package de.lystx.cloudsystem.library.elements.packets.in.serverselector;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.utils.Document;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInRemoveNPC extends Packet implements Serializable {

    private final String key;

    public PacketPlayInRemoveNPC(String key) {
        super(PacketPlayInRemoveNPC.class);
        this.key = key;
    }

}
