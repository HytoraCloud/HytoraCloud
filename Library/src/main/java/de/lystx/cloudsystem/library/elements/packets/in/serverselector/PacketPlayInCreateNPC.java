package de.lystx.cloudsystem.library.elements.packets.in.serverselector;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.elements.other.Document;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInCreateNPC extends Packet implements Serializable {

    private final String key;
    private final VsonObject vsonObject;

    public PacketPlayInCreateNPC(String key, VsonObject vsonObject) {
        super();
        this.key = key;
        this.vsonObject = vsonObject;
    }
}
