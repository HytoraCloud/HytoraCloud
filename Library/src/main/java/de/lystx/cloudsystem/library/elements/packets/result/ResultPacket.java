package de.lystx.cloudsystem.library.elements.packets.result;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public abstract class ResultPacket extends Packet implements Serializable {

    protected UUID uniqueId;
    protected Result result;

    public abstract VsonObject read(CloudLibrary cloudLibrary);

    public ResultPacket uuid(UUID uuid) {
        this.setUniqueId(uuid);
        return this;
    }
}
