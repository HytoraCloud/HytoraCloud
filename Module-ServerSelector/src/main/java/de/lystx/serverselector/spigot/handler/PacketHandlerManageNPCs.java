package de.lystx.serverselector.spigot.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketInformation;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.serverselector.spigot.manager.npc.impl.NPC;
import de.lystx.serverselector.spigot.SpigotSelector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Map;

public class PacketHandlerManageNPCs extends PacketHandlerAdapter {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInformation) {
            PacketInformation information = (PacketInformation) packet;

            if (information.getKey().equalsIgnoreCase("createNPC")) {

                CloudPlayer player = CloudAPI.getInstance().getCloudPlayers().get((String) information.getData().get("player"));
                String skin = (String) information.getData().get("skin");
                String name = (String) information.getData().get("name");

                NPC npc = SpigotSelector.getInstance().getNpcManager().getNPC(Location.deserialize((Map<String, Object>) information.getData().get("loc")));
                if (npc != null) {
                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThere is already an §eNPC §cfor this location!");
                    return;
                }
                ServiceGroup group = CloudAPI.getInstance().getNetwork().getServiceGroup((String) information.getData().get("group"));
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
                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You created an NPC for the group §b" + group.getName() + " §7with skin §b" + skin + "§8!");
                } else {
                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + information.getData().get("group") + " §cdoesn't exist!");
                }
            }
        }
    }
}
