package de.lystx.cloudsystem.library.elements.packets.result;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * This class is used to get
 * real-time information from any CloudInstance
 * to another CloudInstance
 *
 * Modifires : {@link UUID} and {@link Result}
 *
 * A random UUID will be created for the {@link ResultPacket}
 * and the method {@link ResultPacket#read(CloudLibrary)} will be called
 * if the Packet is handled on the other CloudInstance
 * and returns the GenericType of this Packet
 */
@Getter @Setter
public abstract class ResultPacket<R> extends Packet implements Serializable {

    protected UUID uniqueId;
    protected Result<R> result;

    /**
     * Returns GenericType of this Packet
     * @param cloudLibrary
     * @return
     */
    public abstract R read(CloudLibrary cloudLibrary);

    /**
     * Sets the UUID of this ResultPacket
     * @param uuid
     * @return
     */
    public ResultPacket<R> uuid(UUID uuid) {
        this.setUniqueId(uuid);
        return this;
    }
}
