package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketHandlerBukkitStop extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerBukkitStop(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutStopServer) {
            PacketPlayOutStopServer packetPlayOutStopServer = (PacketPlayOutStopServer)packet;
            Service service = packetPlayOutStopServer.getService();
            if (service.getName().equalsIgnoreCase(cloudAPI.getService().getName())) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    Bukkit.getScheduler().runTask(CloudServer.getInstance(), () -> onlinePlayer.kickPlayer(this.cloudAPI.getNetworkConfig().getMessageConfig().getServerShutdownMessage().replace("&", "§").replace("%prefix%", this.cloudAPI.getPrefix())));
                }
                cloudAPI.getScheduler().scheduleDelayedTask(Bukkit::shutdown, 1L);
            }
        }
    }
}
