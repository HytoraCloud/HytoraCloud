package de.lystx.hytoracloud.module.serverselector.spigot.handler;

import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketServiceUpdate;

import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

public class PacketHandlerUpdate implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            Service service = packetServiceUpdate.getService();
            SpigotSelector.getInstance().getSignManager().getSignUpdater().update(service);
        }
    }
}
