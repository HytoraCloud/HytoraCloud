package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.SneakyThrows;

public class BridgeHandlerConfig implements PacketHandler {


    
    @SneakyThrows
    public void handle(Packet packet) {
        if (packet instanceof PacketOutGlobalInfo) {
            PacketOutGlobalInfo packetOutGlobalInfo = ((PacketOutGlobalInfo) packet);

            //Config
            CloudDriver.getInstance().setNetworkConfig(packetOutGlobalInfo.getNetworkConfig());

            //Service
            CloudDriver.getInstance().getServiceManager().setCachedObjects(packetOutGlobalInfo.getServices());

            //Player
            CloudDriver.getInstance().getPlayerManager().setCachedObjects(packetOutGlobalInfo.getCloudPlayers());

            //Receivers
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
