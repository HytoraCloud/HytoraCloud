package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.function.Consumer;

@Getter @Setter
public class PacketCommunication extends Packet implements Serializable {

    private boolean sendBack;

    public PacketCommunication() {}

    public PacketCommunication(Class<?> clazz) {
        this(clazz, true);
    }

    public PacketCommunication(Class<?> clazz, boolean sendBack) {
        super();
        this.sendBack = sendBack;
    }


    public PacketCommunication setSendBack(boolean sendBack) {
        this.sendBack = sendBack;
        return this;
    }
}
