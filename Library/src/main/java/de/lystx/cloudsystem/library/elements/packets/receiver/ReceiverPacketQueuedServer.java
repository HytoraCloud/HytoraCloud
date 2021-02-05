package de.lystx.cloudsystem.library.elements.packets.receiver;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

@Getter
public class ReceiverPacketQueuedServer extends Packet {

    private final Service service;

    public ReceiverPacketQueuedServer(Service service) {
        this.service = service;
    }
}
