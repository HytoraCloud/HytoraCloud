package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.SneakyThrows;

public class BridgeHandlerConfig implements PacketHandler {


    @SneakyThrows
    public void handle(Packet packet) {
        if (packet instanceof PacketOutGlobalInfo) {
            PacketOutGlobalInfo packetOutGlobalInfo = ((PacketOutGlobalInfo) packet);

            //Config
            CloudDriver.getInstance().getConfigManager().setNetworkConfig(packetOutGlobalInfo.getNetworkConfig());

            //Groups
            CloudDriver.getInstance().getGroupManager().setCachedObjects(packetOutGlobalInfo.getGroups());

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
            CloudDriver.getInstance().getConfigManager().setNetworkConfig(packetUpdateNetworkConfig.getNetworkConfig());
        }
    }

}
