package de.lystx.hytoracloud.bridge.bukkit.handler;

import de.lystx.hytoracloud.driver.elements.packets.UUIDPacket;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPlayerLocation;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPlayerWorld;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.impl.response.ResponseStatus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PacketHandlerBukkitRequest implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof UUIDPacket) {

            UUID uuid = ((UUIDPacket) packet).getUuid();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {

                if (packet instanceof PacketRequestPlayerWorld) {

                    World world = player.getWorld();
                    packet.respond(ResponseStatus.SUCCESS, world.getName(), world.getUID());

                } else if (packet instanceof PacketRequestPlayerLocation) {

                    Location location = player.getLocation();
                    packet.respond(ResponseStatus.SUCCESS, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.getWorld().getName());
                }
            } else {
                packet.respond(ResponseStatus.NOT_FOUND);
            }
        }
    }
}
