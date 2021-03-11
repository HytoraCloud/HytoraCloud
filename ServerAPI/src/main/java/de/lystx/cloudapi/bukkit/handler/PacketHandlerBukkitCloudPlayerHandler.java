package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.both.PacketPlaySound;
import de.lystx.cloudsystem.library.elements.packets.both.PacketSendTitle;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@Getter @AllArgsConstructor
public class PacketHandlerBukkitCloudPlayerHandler extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlaySound) {
            PacketPlaySound packetPlaySound = (PacketPlaySound)packet;
            Player player = Bukkit.getPlayer(packetPlaySound.getName());
            if (player == null) {
                return;
            }
            Sound sound = Sound.valueOf(packetPlaySound.getSound());
            player.playSound(player.getLocation(), sound, packetPlaySound.getV1(), packetPlaySound.getV2());
        } else if (packet instanceof PacketSendTitle) {
            PacketSendTitle packetSendTitle = (PacketSendTitle)packet;
            Player player = Bukkit.getPlayer(packetSendTitle.getName());
            if (player == null) {
                return;
            }
            player.sendTitle(packetSendTitle.getTitle(), packetSendTitle.getSubtitle());
        }
    }
}
