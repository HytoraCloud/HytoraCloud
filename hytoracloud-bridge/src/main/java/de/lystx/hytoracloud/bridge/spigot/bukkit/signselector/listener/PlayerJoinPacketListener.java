package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.listener;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl.PacketReader;
import de.lystx.hytoracloud.driver.CloudDriver;
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
    }
}
