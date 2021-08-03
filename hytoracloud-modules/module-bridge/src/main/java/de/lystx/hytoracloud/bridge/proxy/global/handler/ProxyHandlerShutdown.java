package de.lystx.hytoracloud.bridge.proxy.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.service.IService;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;


public class ProxyHandlerShutdown implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (!(packet instanceof PacketOutStopServer)) {
            return;
        }
        PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
        IService IService = CloudDriver.getInstance().getServiceManager().getCachedObject(packetOutStopServer.getService());
        if (IService.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
            for (de.lystx.hytoracloud.driver.player.ICloudPlayer ICloudPlayer : CloudDriver.getInstance().getPlayerManager()) {
                ICloudPlayer.kick(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getBukkitShutdown().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix()));
            }
            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().stopProxy(), 1L);
        }
    }
}
