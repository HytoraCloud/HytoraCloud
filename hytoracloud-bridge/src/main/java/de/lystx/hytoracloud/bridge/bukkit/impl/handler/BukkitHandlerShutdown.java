package de.lystx.hytoracloud.bridge.bukkit.impl.handler;

import de.lystx.hytoracloud.bridge.bukkit.BukkitBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class BukkitHandlerShutdown implements PacketHandler {


    
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            if (packetOutStopServer.getService().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {
                BukkitBridge.getInstance().shutdown();
            }
        }
    }
}
