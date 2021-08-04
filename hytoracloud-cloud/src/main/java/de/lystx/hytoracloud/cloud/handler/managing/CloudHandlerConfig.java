package de.lystx.hytoracloud.cloud.handler.managing;

import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.config.IConfigManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;



public class CloudHandlerConfig implements IPacketHandler {


    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketUpdateNetworkConfig) {

            IConfigManager configManager = CloudDriver.getInstance().getConfigManager();
            PacketUpdateNetworkConfig packetUpdateNetworkConfig = (PacketUpdateNetworkConfig)packet;
            NetworkConfig config = packetUpdateNetworkConfig.getNetworkConfig();

            configManager.setNetworkConfig(config);
            configManager.shutdown();
            configManager.reload();
            CloudDriver.getInstance().sendPacket(packet);
        }
    }
}
