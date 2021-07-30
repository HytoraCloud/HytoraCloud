package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.bridge.spigot.bukkit.BukkitBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

public class BukkitHandlerShutdown implements PacketHandler {


    
    public void handle(Packet packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            if (packetOutStopServer.getService().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                BukkitBridge.getInstance().shutdown();
            }
        }
    }
}
