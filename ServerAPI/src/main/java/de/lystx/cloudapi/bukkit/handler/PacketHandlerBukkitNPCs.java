package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutGlobalInfo;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutNPCs;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.packet.enums.PacketPriority;
import de.lystx.cloudsystem.library.service.packet.raw.PacketHandler;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@AllArgsConstructor
public class PacketHandlerBukkitNPCs {

    private final CloudAPI cloudAPI;

    @PacketHandler(priority = PacketPriority.HIGH)
    public void handlePacket(PacketPlayOutNPCs packet) {
        if (CloudServer.getInstance().isNewVersion()) {
            return;
        }
        CloudServer.getInstance().getNpcManager().setNpcConfig(packet.getNpcConfig());
        CloudServer.getInstance().getNpcManager().setDocument(packet.getDocument());
        CloudServer.getInstance().getNpcManager().updateNPCS();
    }

}
