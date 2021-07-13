package de.lystx.hytoracloud.bridge.bukkit.signselector.listener;

import de.lystx.hytoracloud.bridge.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.bridge.bukkit.signselector.manager.npc.impl.PacketReader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerQuitPacketListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PacketReader packetReader = ServerSelector.PACKET_READERS.getOrDefault(player.getUniqueId(), new PacketReader(player));
        packetReader.uninject();
        ServerSelector.PACKET_READERS.remove(player.getUniqueId());
    }

}
