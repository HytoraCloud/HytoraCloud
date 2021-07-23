package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.commons.receiver.DefaultReceiverManager;
import de.lystx.hytoracloud.driver.commons.service.IService;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.SneakyThrows;

public class BridgeHandlerConfig implements PacketHandler {


    
    @SneakyThrows
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketOutGlobalInfo) {
            PacketOutGlobalInfo packetOutGlobalInfo = ((PacketOutGlobalInfo) packet);

            CloudDriver.getInstance().setNetworkConfig(packetOutGlobalInfo.getNetworkConfig());
            CloudDriver.getInstance().getServiceManager().setCachedObjects(packetOutGlobalInfo.getServices());
            CloudDriver.getInstance().getReceiverManager().getAvailableReceivers().clear();
            CloudDriver.getInstance().getReceiverManager().getAvailableReceivers().addAll(packetOutGlobalInfo.getReceivers());

        } else if (packet instanceof PacketOutRegisterServer) {

            PacketOutRegisterServer packetOutRegisterServer = (PacketOutRegisterServer)packet;
            CloudDriver.getInstance().getServiceManager().registerService(packetOutRegisterServer.getService());

        } else if (packet instanceof PacketUpdateNetworkConfig) {

            PacketUpdateNetworkConfig packetUpdateNetworkConfig = (PacketUpdateNetworkConfig)packet;
            CloudDriver.getInstance().setNetworkConfig(packetUpdateNetworkConfig.getNetworkConfig());
        }
    }

}
