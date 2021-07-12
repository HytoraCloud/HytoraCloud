package de.lystx.hytoracloud.launcher.cloud.handler.receiver;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.packets.out.receiver.PacketReceiverLogin;
import de.lystx.hytoracloud.driver.commons.packets.out.receiver.PacketReceiverShutdown;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CloudHandlerReceiver implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketReceiverShutdown) {
            PacketReceiverShutdown packetReceiverShutdown = (PacketReceiverShutdown)packet;
            cloudSystem.getReceiverManager().unregisterReceiver(packetReceiverShutdown.getReceiverInfo());

        } else if (packet instanceof PacketReceiverLogin) {
            PacketReceiverLogin packetReceiverLogin = (PacketReceiverLogin)packet;
            cloudSystem.getReceiverManager().registerReceiver(packetReceiverLogin.getKey(), packetReceiverLogin.getReceiverInfo());

        }
    }
}
