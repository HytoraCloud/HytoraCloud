package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.bridge.spigot.bukkit.BukkitBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.out.PacketOutStopServer;



public class BukkitHandlerShutdown implements IPacketHandler {


    
    public void handle(IPacket packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            if (packetOutStopServer.getService().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
                BukkitBridge.getInstance().shutdown();
            }
        }
    }
}
