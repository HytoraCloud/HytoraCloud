package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.both.PacketCommunication;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@AllArgsConstructor
public class PacketHandlerCommunication extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunication) {
            PacketCommunication packetCommunication = (PacketCommunication)packet;
            if (packetCommunication.isSendBack()) {
                packetCommunication.setSendBack(false);
                this.cloudAPI.sendPacket(packetCommunication);
            }
        }
    }

}
