package de.lystx.hytoracloud.launcher.cloud.handler.managing;

import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class CloudHandlerConfig implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketUpdateNetworkConfig) {

            ConfigService configService = CloudDriver.getInstance().getInstance(ConfigService.class);
            PacketUpdateNetworkConfig packetUpdateNetworkConfig = (PacketUpdateNetworkConfig)packet;
            NetworkConfig config = packetUpdateNetworkConfig.getNetworkConfig();

            configService.setNetworkConfig(config);
            configService.saveAndReload();

        }
    }
}
