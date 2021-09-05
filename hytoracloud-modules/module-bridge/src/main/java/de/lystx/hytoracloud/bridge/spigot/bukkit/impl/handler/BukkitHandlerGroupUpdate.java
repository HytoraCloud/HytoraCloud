package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;



import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitHandlerGroupUpdate implements IPacketHandler {

    
    public void handle(IPacket packet) {
        if (packet instanceof PacketInUpdateServiceGroup) {

            PacketInUpdateServiceGroup packetPlayOutUpdateServiceGroup = (PacketInUpdateServiceGroup) packet;
            IServiceGroup group = CloudDriver.getInstance().getServiceManager().getThisService().getGroup();
            IServiceGroup newGroup = packetPlayOutUpdateServiceGroup.getServiceGroup();

            if (newGroup.getName().equalsIgnoreCase(group.getName())) {
                if (newGroup.isMaintenance()) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission("cloudsystem.group.maintenance")) {
                            continue;
                        }
                        onlinePlayer.kickPlayer(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getMaintenanceGroup().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix())
                                .replace("%group%", group.getName()));
                    }
                }
            }
        }
    }
}
