package de.lystx.cloudsystem.library.elements.packets;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunication;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class CustomPacket extends PacketCommunication implements Serializable {

     public CustomPacket(Packet packet) {
        this.append("packet", packet);
        this.setSendBack(true);
     }

     public Packet getPacket() {
         return (Packet) this.document().get("packet");
     }
}
