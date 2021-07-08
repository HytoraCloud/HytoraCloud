package de.lystx.hytoracloud.launcher.cloud.handler.managing;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import io.thunder.packet.handler.PacketHandler;
import de.lystx.hytoracloud.driver.service.config.stats.StatsService;

import de.lystx.hytoracloud.driver.elements.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.config.impl.NetworkConfig;
import io.thunder.packet.Packet;

public class PacketHandlerConfig implements PacketHandler {

    private final CloudSystem cloudSystem;

    public PacketHandlerConfig(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    
    public void handle(Packet packet) {
        if (packet instanceof PacketUpdateNetworkConfig) {

            PacketUpdateNetworkConfig packetUpdateNetworkConfig = (PacketUpdateNetworkConfig)packet;
            boolean mc = this.cloudSystem.getInstance(ConfigService.class).getNetworkConfig().getGlobalProxyConfig().isMaintenance();
            NetworkConfig config = packetUpdateNetworkConfig.getNetworkConfig();

            this.cloudSystem.getInstance(ConfigService.class).setNetworkConfig(config);
            this.cloudSystem.getInstance(ConfigService.class).save();
            this.cloudSystem.getInstance(ConfigService.class).reload();

            if (mc != config.getGlobalProxyConfig().isMaintenance()) {
                this.cloudSystem.getInstance(StatsService.class).getStatistics().add("maintenanceSwitched");
            }
        }
    }
}
