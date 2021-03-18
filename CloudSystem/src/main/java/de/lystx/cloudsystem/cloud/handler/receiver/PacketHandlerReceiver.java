package de.lystx.cloudsystem.cloud.handler.receiver;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLogin;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverShutdown;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerReceiver {

    private final CloudSystem cloudSystem;

    @PacketHandler
    public void handleExit(PacketReceiverShutdown packet) {
        cloudSystem.getReceiverManager().unregisterReceiver(packet.getReceiverInfo());
    }

    @PacketHandler
    public void handleReceiverLogin(PacketReceiverLogin packet) {
        cloudSystem.getReceiverManager().registerReceiver(packet.getKey(), packet.getReceiverInfo());
    }

}
