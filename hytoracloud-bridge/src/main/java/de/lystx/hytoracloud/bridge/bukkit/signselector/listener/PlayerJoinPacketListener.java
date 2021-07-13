package de.lystx.hytoracloud.bridge.bukkit.signselector.listener;

import de.lystx.hytoracloud.bridge.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.bridge.bukkit.signselector.manager.npc.impl.PacketReader;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.NoSuchElementException;

public class PlayerJoinPacketListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        //NPCs injecting for InteractEvent
        if (!CloudDriver.getInstance().getBukkit().isNewVersion()) {
            PacketReader packetReader = new PacketReader(player);
            try {
                packetReader.inject();
                ServerSelector.PACKET_READERS.put(player.getUniqueId(), packetReader);
                ServerSelector.getInstance().getNpcManager().updateNPCS(player, true);

            } catch (NoSuchElementException e){
                e.printStackTrace();
            }
        }
        if (CloudDriver.getInstance().isNametags() && CloudDriver.getInstance().getPermissionPool().isAvailable()) {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PermissionGroup group = CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(onlinePlayer.getUniqueId());
                if (group == null) {
                    System.out.println("[CloudBridge] Couldn't update Nametag for " + player.getName() + "! His PermissionGroup couldn't be found!");
                    return;
                }
                BukkitBridge.getInstance().getNametagManager().setNametag(group.getPrefix(), group.getSuffix(), group.getId(), player.getName());
            }
        }
    }
}
