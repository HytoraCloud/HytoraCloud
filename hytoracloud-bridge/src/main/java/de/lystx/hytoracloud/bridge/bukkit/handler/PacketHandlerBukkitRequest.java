package de.lystx.hytoracloud.bridge.bukkit.handler;

import de.lystx.hytoracloud.driver.elements.packets.UUIDPacket;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPlayerLocation;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPlayerWorld;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PacketHandlerBukkitRequest implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof UUIDPacket) {

            UUID uuid = ((UUIDPacket) packet).getUuid();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {

                if (packet instanceof PacketRequestPlayerWorld) {

                    World world = player.getWorld();
                    packet.reply(component -> {
                        component.put("name", world.getName());
                        component.put("id", world.getUID());
                    });

                } else if (packet instanceof PacketRequestPlayerLocation) {

                    Location location = player.getLocation();
                    packet.reply(component -> {
                        component.put("x", location.getX());
                        component.put("y", location.getY());
                        component.put("z", location.getZ());
                        component.put("yaw", location.getYaw());
                        component.put("pitch", location.getPitch());
                        component.put("world", location.getWorld().getName());
                    });

                }
            } else {
                packet.reply(ResponseStatus.NOT_FOUND);
            }
        }
    }
}
