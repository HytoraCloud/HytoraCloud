package de.lystx.cloudsystem.cloud.handler.receiver;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLoginResult;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLogin;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverShutdown;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.util.Decision;
import lombok.AllArgsConstructor;

import java.util.UUID;

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
