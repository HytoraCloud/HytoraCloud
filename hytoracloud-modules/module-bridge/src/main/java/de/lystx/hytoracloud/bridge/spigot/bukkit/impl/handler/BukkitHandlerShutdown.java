package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.bridge.spigot.bukkit.BukkitBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

public class BukkitHandlerShutdown implements PacketHandler {


    
    public void handle(Packet packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            if (packetOutStopServer.getService().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
                BukkitBridge.getInstance().shutdown();
            }
        }
    }
}
