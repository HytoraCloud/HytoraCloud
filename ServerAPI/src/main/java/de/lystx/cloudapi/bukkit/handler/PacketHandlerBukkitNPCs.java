package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketOutNPC;
import de.lystx.cloudsystem.library.enums.Priority;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerBukkitNPCs {

    private final CloudAPI cloudAPI;

    @PacketHandler(Priority.HIGH)
    public void handlePacket(PacketOutNPC packet) {
        if (CloudServer.getInstance().isNewVersion()) {
            return;
        }
        CloudServer.getInstance().getNpcManager().setNpcConfig(packet.getNpcConfig());
        CloudServer.getInstance().getNpcManager().setDocument(packet.getDocument());
        CloudServer.getInstance().getNpcManager().updateNPCS();
    }

}
