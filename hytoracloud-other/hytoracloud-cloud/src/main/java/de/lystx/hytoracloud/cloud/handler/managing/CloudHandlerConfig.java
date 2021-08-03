package de.lystx.hytoracloud.cloud.handler.managing;

import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.config.IConfigManager;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

public class CloudHandlerConfig implements PacketHandler {


    @Override
    public void handle(Packet packet) {
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
