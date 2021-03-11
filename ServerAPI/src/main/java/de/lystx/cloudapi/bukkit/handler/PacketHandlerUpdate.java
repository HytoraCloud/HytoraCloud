package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerUpdate {

    private final CloudAPI cloudAPI;

    @PacketHandler
    public void handle(PacketInServiceUpdate packet) {
        CloudServer.getInstance().getSignManager().getSignUpdater().update(packet.getService());
    }
}
