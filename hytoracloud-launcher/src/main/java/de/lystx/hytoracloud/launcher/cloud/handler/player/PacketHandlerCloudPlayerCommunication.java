package de.lystx.hytoracloud.launcher.cloud.handler.player;


import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerCloudPlayerCommunication implements PacketHandler {

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
