package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationPlaySound;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSendTitle;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@Getter
public class PacketHandlerBukkitCloudPlayerHandler extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerBukkitCloudPlayerHandler(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunicationPlaySound) {
            PacketCommunicationPlaySound packetCommunicationPlaySound = (PacketCommunicationPlaySound)packet;
            Player player = Bukkit.getPlayer(packetCommunicationPlaySound.getName());
            if (player == null) {
                return;
            }
            Sound sound = Sound.valueOf(packetCommunicationPlaySound.getSound());
            player.playSound(player.getLocation(), sound, packetCommunicationPlaySound.getV1(), packetCommunicationPlaySound.getV2());
        } else if (packet instanceof PacketCommunicationSendTitle) {
            PacketCommunicationSendTitle packetCommunicationSendTitle = (PacketCommunicationSendTitle)packet;
            Player player = Bukkit.getPlayer(packetCommunicationSendTitle.getName());
            if (player == null) {
                return;
            }
            player.sendTitle(packetCommunicationSendTitle.getTitle(), packetCommunicationSendTitle.getSubtitle());
        }
    }
}
