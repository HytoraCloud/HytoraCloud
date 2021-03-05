package de.lystx.cloudsystem.cloud.handler.managing;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInNetworkConfig;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
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
            boolean mc = this.cloudSystem.getService(ConfigService.class).getNetworkConfig().getNetworkConfig().isMaintenance();
            NetworkConfig config = packetPlayInNetworkConfig.getNetworkConfig();
            this.cloudSystem.getService(ConfigService.class).setNetworkConfig(config);
            this.cloudSystem.getService(ConfigService.class).save();
            this.cloudSystem.getService(ConfigService.class).reload();
            if (mc != config.getNetworkConfig().isMaintenance()) {
                this.cloudSystem.getService(StatisticsService.class).getStatistics().add("maintenanceSwitched");
            }
            this.cloudSystem.reload();
        }
    }
}
