package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.handler;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.service.IService;



public class BukkitHandlerSignUpdate implements IPacketHandler {


    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            IService service = packetServiceUpdate.getService();
            CloudDriver.getInstance().getExecutorService().execute(() -> ServerSelector.getInstance().getSignManager().getSignUpdater().update(service));
        }
    }
}
