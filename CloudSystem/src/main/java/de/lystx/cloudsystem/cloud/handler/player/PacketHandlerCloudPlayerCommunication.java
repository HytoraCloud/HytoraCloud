package de.lystx.cloudsystem.cloud.handler.player;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCommunication;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@AllArgsConstructor
public class PacketHandlerCloudPlayerCommunication extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunication) {
            PacketCommunication packetCommunication = (PacketCommunication)packet;
            if (packetCommunication.isSendBack()) {
                this.cloudSystem.sendPacket(packetCommunication.setSendBack(false));
            }
        }
    }
}
