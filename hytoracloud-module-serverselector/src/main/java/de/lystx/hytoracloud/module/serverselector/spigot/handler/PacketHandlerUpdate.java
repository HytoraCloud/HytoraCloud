package de.lystx.hytoracloud.module.serverselector.spigot.handler;

import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;

import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class PacketHandlerUpdate implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            Service service = packetServiceUpdate.getService();
            SpigotSelector.getInstance().getSignManager().getSignUpdater().update(service);
        }
    }
}
