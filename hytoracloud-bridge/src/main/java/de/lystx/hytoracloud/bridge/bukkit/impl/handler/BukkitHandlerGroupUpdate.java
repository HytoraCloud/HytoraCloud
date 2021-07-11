package de.lystx.hytoracloud.bridge.bukkit.impl.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitHandlerGroupUpdate implements PacketHandler {

    
    public void handle(HytoraPacket packet) {
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
        }
    }
}
