package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import net.md_5.bungee.api.ProxyServer;

public class PacketHandlerProxyStop implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (!(packet instanceof PacketOutStopServer)) {
            return;
        }
        PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
        Service service = CloudDriver.getInstance().getServiceManager().getService(packetOutStopServer.getService());
        if (service.getName().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {
            ProxyServer.getInstance().getPlayers().forEach(player ->  player.disconnect(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServerShutdownMessage().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix())));
            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(ProxyServer.getInstance()::stop, 1L);
        }
    }
}
