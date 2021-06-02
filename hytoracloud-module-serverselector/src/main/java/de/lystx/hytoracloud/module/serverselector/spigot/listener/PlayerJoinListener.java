package de.lystx.hytoracloud.module.serverselector.spigot.listener;

import de.lystx.hytoracloud.bridge.bukkit.CloudServer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
import de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.impl.PacketReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.NoSuchElementException;

public class PlayerJoinListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        //NPCs injecting for InteractEvent
        if (!CloudDriver.getInstance().getBukkit().isNewVersion()) {
            PacketReader packetReader = new PacketReader(player);
            try {
                packetReader.inject();
                SpigotSelector.PACKET_READERS.put(player.getUniqueId(), packetReader);
                SpigotSelector.getInstance().getNpcManager().updateNPCS(SpigotSelector.getInstance().getNpcManager().getJsonBuilder(), player, true);

            } catch (NoSuchElementException e){
                e.printStackTrace();
            }
        }
        if (CloudDriver.getInstance().isNametags() && CloudDriver.getInstance().getPermissionPool().isAvailable()) {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PermissionGroup group = CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(onlinePlayer.getUniqueId());
                if (group == null) {
                    System.out.println("[CloudAPI] Couldn't update Nametag for " + player.getName() + "! His PermissionGroup couldn't be found!");
                    return;
                }
                CloudServer.getInstance().getNametagManager().setNametag(group.getPrefix(), group.getSuffix(), group.getId(), player.getName());
            }
        }
    }
}
