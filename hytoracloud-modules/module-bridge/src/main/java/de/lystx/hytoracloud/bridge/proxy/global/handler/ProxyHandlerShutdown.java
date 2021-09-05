package de.lystx.hytoracloud.bridge.proxy.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.service.IService;





public class ProxyHandlerShutdown implements IPacketHandler {


    @Override
    public void handle(IPacket packet) {
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
