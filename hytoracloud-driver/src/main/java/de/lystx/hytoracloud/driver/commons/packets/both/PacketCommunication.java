package de.lystx.hytoracloud.driver.commons.packets.both;



import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

/**
 * This packet will be sent from
 * Server to CLient and from
 * Client to Server
 * > It communicates through the whole
 * Network...
 */
@Getter @Setter
public abstract class PacketCommunication extends HytoraPacket {


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
    public void read(Component component) {
        if (component == null) {
            sendBack = false;
            return;
        }
        sendBack = component.get("b");
    }

    @Override
    public void write(Component component) {
        component.append(map -> map.put("b", sendBack));
    }
}
