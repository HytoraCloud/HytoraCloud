package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutGlobalInfo;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.elements.other.Document;
import org.bukkit.Bukkit;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

public class PacketHandlerBukkitNPCs extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerBukkitNPCs(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutGlobalInfo) {
            if (CloudServer.getInstance().isNewVersion()) {
                return;
            }
            PacketPlayOutGlobalInfo info = (PacketPlayOutGlobalInfo) packet;
            CloudServer.getInstance().getNpcManager().setNpcConfig(info.getNpcConfig());
            Bukkit.getOnlinePlayers().forEach(player -> CloudServer.getInstance().getNpcManager().updateNPCS(new Document(info.getNpcs()), player));
        }
    }
}
