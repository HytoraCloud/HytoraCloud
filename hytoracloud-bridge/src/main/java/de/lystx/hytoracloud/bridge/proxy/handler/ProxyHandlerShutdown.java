package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;


public class ProxyHandlerShutdown implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (!(packet instanceof PacketOutStopServer)) {
            return;
        }
        PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
        Service service = CloudDriver.getInstance().getServiceManager().getService(packetOutStopServer.getService());
        if (service.getName().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {
            for (CloudPlayer cloudPlayer : CloudDriver.getInstance().getCloudPlayerManager()) {
                cloudPlayer.kick(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServerShutdownMessage().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix()));
            }
            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudBridge.getInstance().getProxyBridge().stopProxy(), 1L);
        }
    }
}
