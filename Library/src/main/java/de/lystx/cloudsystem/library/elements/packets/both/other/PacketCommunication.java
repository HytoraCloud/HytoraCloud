package de.lystx.cloudsystem.library.elements.packets.both.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * //TODO: DOCUMENTATION
 */
@Getter @Setter
public class PacketCommunication extends Packet implements Serializable {

    private boolean sendBack;

    public PacketCommunication() {
        this.sendBack = true;
    }

    /**
     * Declares that the packet will be sent back
     * @param sendBack
     * @return current Packet
     */
    public PacketCommunication setSendBack(boolean sendBack) {
        this.sendBack = sendBack;
        return this;
    }
}
