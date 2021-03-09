package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInServiceStateChange;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInUpdateServiceGroup;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.elements.other.Document;
import io.vson.elements.object.VsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

public class PacketHandlerBukkitServerUpdate extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerBukkitServerUpdate(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInUpdateServiceGroup) {
            PacketPlayInUpdateServiceGroup packetPlayOutUpdateServiceGroup = (PacketPlayInUpdateServiceGroup)packet;
            ServiceGroup group = this.cloudAPI.getService().getServiceGroup();
            ServiceGroup newGroup = packetPlayOutUpdateServiceGroup.getServiceGroup();
            if (newGroup.getName().equalsIgnoreCase(group.getName())) {
                if (newGroup.isMaintenance()) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission("cloudsystem.group.maintenance")) {
                            continue;
                        }
                        onlinePlayer.kickPlayer(this.cloudAPI.getNetworkConfig().getMessageConfig().getGroupMaintenanceMessage().replace("&", "§").replace("%prefix%", this.cloudAPI.getPrefix())
                                .replace("%group%", group.getName()));
                    }
                }
                CloudAPI.getInstance().setMaxPlayers(newGroup.getMaxPlayers());
                CloudAPI.getInstance().update();
            }
        } else if (packet instanceof PacketPlayInServiceStateChange) {
            PacketPlayInServiceStateChange packetPlayInServiceStateChange = (PacketPlayInServiceStateChange)packet;
            Service service = packetPlayInServiceStateChange.getService();
            ServiceState serviceState = packetPlayInServiceStateChange.getServiceState();
            if (cloudAPI.getService().getName().equalsIgnoreCase(service.getName())) {
                VsonObject document = this.cloudAPI.getDocument();
                document.append("serviceState", serviceState);
                document.save();
            }
        }
    }
}
