package de.lystx.hytoracloud.driver.elements.packets.both;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * This packet will be sent from
 * Server to CLient and from
 * Client to Server
 * > It communicates through the whole
 * Network...
 */
@Getter @Setter
public abstract class PacketCommunication extends Packet implements Serializable {

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

    @Override
    public void read(PacketBuffer buf) {
        sendBack = buf.readBoolean();
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeBoolean(sendBack);
    }
}
