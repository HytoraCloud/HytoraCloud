package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


public class ProxyHandlerShutdown implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (!(packet instanceof PacketOutStopServer)) {
            return;
        }
        PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
        IService IService = CloudDriver.getInstance().getServiceManager().getService(packetOutStopServer.getService());
        if (IService.getName().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
            for (ICloudPlayer ICloudPlayer : CloudDriver.getInstance().getCloudPlayerManager()) {
                ICloudPlayer.kick(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getBukkitShutdown().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix()));
            }
            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().stopProxy(), 1L);
        }
    }
}
