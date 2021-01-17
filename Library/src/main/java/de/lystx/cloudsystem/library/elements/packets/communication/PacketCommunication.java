package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class PacketCommunication extends Packet implements Serializable {

    private boolean sendBack;

    public PacketCommunication(Class<?> clazz) {
        this(clazz, true);
    }

    public PacketCommunication(Class<?> clazz, boolean sendBack) {
        super();
        this.sendBack = sendBack;
    }



}
