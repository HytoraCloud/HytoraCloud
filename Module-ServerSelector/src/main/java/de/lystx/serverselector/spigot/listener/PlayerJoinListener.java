package de.lystx.serverselector.spigot.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.serverselector.spigot.manager.npc.impl.PacketReader;
import de.lystx.serverselector.spigot.SpigotSelector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.NoSuchElementException;

public class PlayerJoinListener implements Listener {


    @EventHandler
    public void handle(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        //NPCs injecting for InteractEvent
        if (!CloudAPI.getInstance().isNewVersion()) {
            PacketReader packetReader = new PacketReader(player);
            try {
                packetReader.inject();
                SpigotSelector.PACKET_READERS.put(player.getUniqueId(), packetReader);
                SpigotSelector.getInstance().getNpcManager().updateNPCS(SpigotSelector.getInstance().getNpcManager().getDocument(), player, true);

                if (CloudAPI.getInstance().isNametags() && CloudAPI.getInstance().getPermissionPool().isAvailable()) {

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        PermissionGroup group = CloudAPI.getInstance().getPermissionPool().getHighestPermissionGroup(onlinePlayer.getName());
                        if (group == null) {
                            System.out.println("[CloudAPI] Couldn't update Nametag for " + player.getName() + "! His PermissionGroup couldn't be found!");
                            return;
                        }
                        CloudAPI.getInstance().updateNametag(CloudAPI.getInstance().getCloudPlayers().get(player.getName()), group.getPrefix(), group.getSuffix(), group.getId());
                    }
                }
            } catch (NoSuchElementException e){
                e.printStackTrace();
            }
        }
    }
}
