package de.lystx.cloudsystem.library.elements.packets.receiver;

import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class PacketReceiverLogin extends Packet implements Serializable {

    private final ReceiverInfo receiverInfo;
    private final String key;


}
