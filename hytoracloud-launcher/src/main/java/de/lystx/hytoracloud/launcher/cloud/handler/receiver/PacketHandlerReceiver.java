package de.lystx.hytoracloud.launcher.cloud.handler.receiver;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.packets.receiver.PacketReceiverLogin;
import de.lystx.hytoracloud.driver.elements.packets.receiver.PacketReceiverShutdown;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerReceiver implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketReceiverShutdown) {
            PacketReceiverShutdown packetReceiverShutdown = (PacketReceiverShutdown)packet;
            cloudSystem.getReceiverManager().unregisterReceiver(packetReceiverShutdown.getReceiverInfo());

        } else if (packet instanceof PacketReceiverLogin) {
            PacketReceiverLogin packetReceiverLogin = (PacketReceiverLogin)packet;
            cloudSystem.getReceiverManager().registerReceiver(packetReceiverLogin.getKey(), packetReceiverLogin.getReceiverInfo());

        }
    }
}
