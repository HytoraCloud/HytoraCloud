package de.lystx.cloudsystem.receiver.handler;

import de.lystx.cloudsystem.library.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.cloudsystem.library.elements.packets.out.PacketOutVerifyConnection;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLoginResult;
import de.lystx.cloudsystem.library.enums.Decision;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReceiverPacketHandlerConfig {

    private final Receiver receiver;

    @PacketHandler
    public void handleLogin(PacketOutGlobalInfo packet) {
        final NetworkConfig networkConfig = packet.getNetworkConfig();
        receiver.getService(ConfigService.class).setNetworkConfig(networkConfig);

    }
}
