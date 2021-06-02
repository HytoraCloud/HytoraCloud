package de.lystx.hytoracloud.bridge.bukkit.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

public class PacketHandlerBukkitServerUpdate implements PacketHandler {

    
    public void handle(Packet packet) {
        if (packet instanceof PacketInUpdateServiceGroup) {
            PacketInUpdateServiceGroup packetPlayOutUpdateServiceGroup = (PacketInUpdateServiceGroup) packet;
            ServiceGroup group = CloudDriver.getInstance().getThisService().getServiceGroup();
            ServiceGroup newGroup = packetPlayOutUpdateServiceGroup.getServiceGroup();
            if (newGroup.getName().equalsIgnoreCase(group.getName())) {
                if (newGroup.isMaintenance()) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission("cloudsystem.group.maintenance")) {
                            continue;
                        }
                        onlinePlayer.kickPlayer(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getGroupMaintenanceMessage().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix())
                                .replace("%group%", group.getName()));
                    }
                }
                CloudDriver.getInstance().getBukkit().setMaxPlayers(newGroup.getMaxPlayers());
                CloudDriver.getInstance().getBukkit().update();
            }
        } else if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            Service service = packetServiceUpdate.getService();
            if (service.getName().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {
                JsonBuilder jsonBuilder = new JsonBuilder(new File("./CLOUD/connection.json"));
                jsonBuilder.clear();
                jsonBuilder.append(service);
                jsonBuilder.save();
            }
        }
    }
}
