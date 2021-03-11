package de.lystx.cloudapi.bukkit.listener.player;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.manager.npc.impl.PacketReader;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerQuitListener implements Listener {


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PacketReader packetReader = CloudServer.PACKET_READERS.getOrDefault(player.getUniqueId(), new PacketReader(player));
        packetReader.uninject();
        CloudServer.PACKET_READERS.remove(player.getUniqueId());
        CloudServer.getInstance().startStopTimer();
        CloudAPI.getInstance().sendPacket(new PacketInServiceUpdate(CloudAPI.getInstance().getService()));
    }

}
