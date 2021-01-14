package de.lystx.cloudsystem.handler.managing;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInNetworkConfig;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

public class PacketHandlerConfig extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerConfig(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInNetworkConfig) {
            PacketPlayInNetworkConfig packetPlayInNetworkConfig = (PacketPlayInNetworkConfig)packet;
            NetworkConfig config = packetPlayInNetworkConfig.getNetworkConfig();
            this.cloudSystem.getService(ConfigService.class).setNetworkConfig(config);
            this.cloudSystem.reload("config");
        }
    }
}
