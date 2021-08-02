package de.lystx.hytoracloud.cloud.handler.other;


import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CloudHandlerCommunication implements PacketHandler {

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
