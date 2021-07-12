package de.lystx.hytoracloud.launcher.cloud.handler.managing;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.config.stats.StatsService;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class PacketHandlerConfig implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketUpdateNetworkConfig) {

            ConfigService configService = CloudDriver.getInstance().getInstance(ConfigService.class);
            PacketUpdateNetworkConfig packetUpdateNetworkConfig = (PacketUpdateNetworkConfig)packet;
            NetworkConfig config = packetUpdateNetworkConfig.getNetworkConfig();

            boolean mc = CloudDriver.getInstance().getNetworkConfig().isMaintenance();

            configService.setNetworkConfig(config);
            configService.saveAndReload();

            if (mc != config.isMaintenance()) {
                CloudDriver.getInstance().getInstance(StatsService.class).getStatistics().add("maintenanceSwitched");
            }
        }
    }
}
