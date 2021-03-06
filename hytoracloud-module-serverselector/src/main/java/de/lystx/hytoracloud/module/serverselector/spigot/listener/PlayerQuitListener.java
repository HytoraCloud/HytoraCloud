package de.lystx.hytoracloud.module.serverselector.spigot.listener;

import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
import de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.impl.PacketReader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PacketReader packetReader = SpigotSelector.PACKET_READERS.getOrDefault(player.getUniqueId(), new PacketReader(player));
        packetReader.uninject();
        SpigotSelector.PACKET_READERS.remove(player.getUniqueId());
    }

}
