package de.lystx.hytoracloud.launcher.cloud.handler.player;


import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerCloudPlayerCommunication implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketCommunication) {
            PacketCommunication packetCommunication = (PacketCommunication)packet;
            if (packetCommunication.isSendBack()) {
                this.cloudSystem.sendPacket(packetCommunication.setSendBack(false));
            }
        }
    }
}
