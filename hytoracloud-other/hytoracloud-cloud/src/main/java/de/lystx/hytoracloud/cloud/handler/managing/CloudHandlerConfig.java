package de.lystx.hytoracloud.cloud.handler.managing;

import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

public class CloudHandlerConfig implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketUpdateNetworkConfig) {

            ConfigService configService = CloudDriver.getInstance().getInstance(ConfigService.class);
            PacketUpdateNetworkConfig packetUpdateNetworkConfig = (PacketUpdateNetworkConfig)packet;
            NetworkConfig config = packetUpdateNetworkConfig.getNetworkConfig();

            configService.setNetworkConfig(config);
            configService.save();
            configService.reload();
            CloudDriver.getInstance().sendPacket(packet);
        }
    }
}
