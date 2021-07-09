package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.bridge.standalone.manager.CloudBridgeServiceManager;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutGlobalInfo;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;


import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.SneakyThrows;

public class PacketHandlerConfig implements PacketHandler {


    
    @SneakyThrows
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketOutGlobalInfo) {
            PacketOutGlobalInfo packetOutGlobalInfo = ((PacketOutGlobalInfo)packet);
            CloudDriver.getInstance().getImplementedData().put("networkConfig", packetOutGlobalInfo.getNetworkConfig());
            ((CloudBridgeServiceManager) CloudDriver.getInstance().getServiceManager()).setServiceMap(packetOutGlobalInfo.getServices());

        } else if (packet instanceof PacketUpdateNetworkConfig) {
            PacketUpdateNetworkConfig packetUpdateNetworkConfig = (PacketUpdateNetworkConfig)packet;
            CloudDriver.getInstance().getImplementedData().put("networkConfig", packetUpdateNetworkConfig.getNetworkConfig());
        }
    }

}
