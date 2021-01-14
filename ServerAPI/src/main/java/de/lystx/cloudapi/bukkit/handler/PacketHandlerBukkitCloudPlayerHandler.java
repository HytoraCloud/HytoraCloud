package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationPlaySound;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSendActionbar;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSendTitle;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
        } else if (packet instanceof PacketCommunicationSendActionbar) {
            PacketCommunicationSendActionbar packetCommunicationSendActionbar = (PacketCommunicationSendActionbar)packet;
            Player player = Bukkit.getPlayer(packetCommunicationSendActionbar.getName());
            if (player == null) {
                return;
            }
            PacketPlayOutChat p = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + packetCommunicationSendActionbar.getMessage() + "\"}"), (byte) 2);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(p);

        }
    }
}
