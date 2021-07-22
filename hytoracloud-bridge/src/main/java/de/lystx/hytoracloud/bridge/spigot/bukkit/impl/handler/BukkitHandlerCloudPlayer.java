package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.driver.commons.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketPlaySound;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketSendActionbar;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketSendTitle;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketTeleportPlayer;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BukkitHandlerCloudPlayer implements PacketHandler {


    
    @SneakyThrows
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketPlaySound) {
            PacketPlaySound packetPlaySound = (PacketPlaySound)packet;
            Player player = Bukkit.getPlayer(packetPlaySound.getName());
            if (player == null) {
                return;
            }
            Sound sound = Sound.valueOf(packetPlaySound.getSound());
            player.playSound(player.getLocation(), sound, packetPlaySound.getV1(), packetPlaySound.getV2());

        } else if (packet instanceof PacketTeleportPlayer) {
            PacketTeleportPlayer teleportPlayer = (PacketTeleportPlayer)packet;
            Player player = Bukkit.getPlayer(teleportPlayer.getUuid());
            MinecraftLocation location = teleportPlayer.getLocation();
            if (player == null || location == null) {
                return;
            }

            player.teleport(
                    new Location(
                    Bukkit.getWorld(
                            location.getWorld()
                    ),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch()
                    )
            );

        } else if (packet instanceof PacketSendActionbar) {

            PacketSendActionbar packetSendActionbar = (PacketSendActionbar)packet;
            Player player = Bukkit.getPlayer(packetSendActionbar.getUuid());
            String message = packetSendActionbar.getMessage();

            if (player == null || message == null) {
                return;
            }
            String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
            nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

            if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {

                Method getSpigot = player.getClass().getDeclaredMethod("spigot"); getSpigot.setAccessible(true);
                Object spigot = getSpigot.invoke(player);
                Method sendMessage = spigot.getClass().getDeclaredMethod("sendMessage", ChatMessageType.class, TextComponent.class); sendMessage.setAccessible(true);

                sendMessage.invoke(spigot, ChatMessageType.ACTION_BAR, new TextComponent(message));
                return;
            }

            try {
                Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
                Object craftPlayer = craftPlayerClass.cast(player);

                Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
                Class<?> p = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
                Object packetPlayOutChat;
                Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
                Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

                Method method = null;
                if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

                Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
                packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

                Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
                Object iCraftPlayer = handle.invoke(craftPlayer);
                Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
                Object playerConnection = playerConnectionField.get(iCraftPlayer);
                Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", p);
                sendPacket.invoke(playerConnection, packetPlayOutChat);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
