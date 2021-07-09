package de.lystx.hytoracloud.module.serverselector.spigot.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketInformation;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.impl.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Map;

public class PacketHandlerManageNPCs implements PacketHandler {

    
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketInformation) {
            PacketInformation information = (PacketInformation) packet;

            if (information.getKey().equalsIgnoreCase("createNPC")) {

                CloudPlayer player = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer((String) information.getObjectMap().get("player"));
                String skin = (String) information.getObjectMap().get("skin");
                String name = (String) information.getObjectMap().get("name");

                NPC npc = SpigotSelector.getInstance().getNpcManager().getNPC(Location.deserialize((Map<String, Object>) information.getObjectMap().get("loc")));
                if (npc != null) {
                    player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThere is already an §eNPC §cfor this location!");
                    return;
                }
                ServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup((String) information.getObjectMap().get("group"));
                if (group != null) {
                    SpigotSelector
                            .getInstance()
                            .getNpcManager()
                            .createNPC(
                                    Bukkit.getPlayer(player.getName()).getLocation(),
                                    ChatColor.translateAlternateColorCodes('&', name),
                                    group.getName(),
                                    skin
                            );
                    player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7You created an NPC for the group §b" + group.getName() + " §7with skin §b" + skin + "§8!");
                } else {
                    player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe group §e" + information.getObjectMap().get("group") + " §cdoesn't exist!");
                }
            }
        }
    }
}
