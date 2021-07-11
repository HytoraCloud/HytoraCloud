package de.lystx.hytoracloud.launcher.cloud.handler.managing;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.service.global.config.stats.StatsService;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.service.global.config.ConfigService;
import de.lystx.hytoracloud.driver.service.global.config.impl.NetworkConfig;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class PacketHandlerConfig implements PacketHandler {

    private final CloudSystem cloudSystem;


    @Override
    public void handle(HytoraPacket packet) {
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
