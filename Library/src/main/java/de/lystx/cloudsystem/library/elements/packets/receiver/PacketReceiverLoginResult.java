package de.lystx.cloudsystem.library.elements.packets.receiver;

import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.util.Decision;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor @Getter
public class PacketReceiverLoginResult extends Packet implements Serializable {

    private final ReceiverInfo receiverInfo;
    private final Decision decision;
    private final List<ServiceGroup> serviceGroups;
}
