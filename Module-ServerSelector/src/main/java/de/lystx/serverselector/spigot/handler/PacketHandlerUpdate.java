package de.lystx.serverselector.spigot.handler;

import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.serverselector.spigot.SpigotSelector;

public class PacketHandlerUpdate {


    @PacketHandler
    public void handle(PacketInServiceUpdate packet) {
        SpigotSelector.getInstance().getSignManager().getSignUpdater().update(packet.getService());
    }
}
