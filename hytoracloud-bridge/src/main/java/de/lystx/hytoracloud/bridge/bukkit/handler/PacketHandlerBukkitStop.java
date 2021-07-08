package de.lystx.hytoracloud.bridge.bukkit.handler;

import de.lystx.hytoracloud.bridge.bukkit.HytoraCloudBukkitBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

public class PacketHandlerBukkitStop implements PacketHandler {


    
    public void handle(Packet packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            Service service = CloudDriver.getInstance().getServiceManager().getService(packetOutStopServer.getService());
            if (service.getName().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {
                HytoraCloudBukkitBridge.getInstance().shutdown();
            }
        }
    }
}
