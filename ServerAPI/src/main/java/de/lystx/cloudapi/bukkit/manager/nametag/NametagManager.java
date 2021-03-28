package de.lystx.cloudapi.bukkit.manager.nametag;

import java.lang.reflect.*;
import java.util.*;

import de.lystx.cloudapi.bukkit.utils.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NametagManager {

    /**
     * Sets the Nametag of a player
     *
     * @param prefix
     * @param suffix
     * @param priority
     * @param user
     */
    public void setNametag(String prefix, String suffix, Integer priority, String user) {
        setNametag(prefix, suffix, priority, user, null);
    }

    /**
     * Sets the TabStyle of the
     * player with packets
     *
     * @param prefix
     * @param suffix
     * @param priority
     * @param user
     * @param players
     */
    public void setNametag(String prefix, String suffix, Integer priority, String user, List<Player> players) {
        clearTabStyle(Bukkit.getPlayer(user), priority, players);
        String team_name = priority + Bukkit.getPlayer(user).getName();
        if (team_name.length() > 16) {
            team_name = team_name.substring(0, 16);
        }
        if (suffix.length() > 16) {
            suffix = suffix.substring(0, 16);
        }
        if (prefix.length() > 16) {
            prefix = prefix.substring(0, 16);
        }
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        suffix = ChatColor.translateAlternateColorCodes('&', suffix);
        try {
            setPlayerListName(Bukkit.getPlayer(user), prefix + Bukkit.getPlayer(user).getName() + suffix, players);
            Constructor<?> constructor = Reflections.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor();
            Object packet = constructor.newInstance();
            List<String> contents = new ArrayList<>();
            contents.add(Bukkit.getPlayer(user).getName());
            try {
                Reflections.setField(packet, "a", team_name);
                Reflections.setField(packet, "b", team_name);
                Reflections.setField(packet, "c", prefix);
                Reflections.setField(packet, "d", suffix);
                Reflections.setField(packet, "e", "ALWAYS");
                Reflections.setField(packet, "h", 0);
                Reflections.setField(packet, "g", contents);
            } catch (Exception ex) {
                Reflections.setField(packet, "a", team_name);
                Reflections.setField(packet, "b", team_name);
                Reflections.setField(packet, "c", prefix);
                Reflections.setField(packet, "d", suffix);
                Reflections.setField(packet, "e", "ALWAYS");
                Reflections.setField(packet, "i", 0);
                Reflections.setField(packet, "h", contents);
            }
            if (players == null) {
                Bukkit.getOnlinePlayers().forEach(t -> this.sendPacket(t, packet));
            } else {
                players.forEach(t -> this.sendPacket(t, packet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setPlayerListName(Player user, String name, List<Player> ps) {
        /*try {
            if (ps == null) {
                ps = new LinkedList<>(Bukkit.getOnlinePlayers());
            }

            Method getHandle = user.getClass().getMethod("getHandle", (Class<?>[]) null);
            Object entityPlayer = getHandle.invoke(user);
            Method method = Reflections.getCustomClass("org.bukkit.craftbukkit.%d%.util.CraftChatMessage").getMethod("fromString", String.class);
            Object[] iChatBaseComponent = (Object[]) method.invoke(null, name);

            Reflections.setField(entityPlayer, "listName", name.equals(user.getName()) ? null : iChatBaseComponent[0]);

            for (Player p : ps) {
                Method handle = p.getClass().getMethod("getHandle", (Class<?>[]) null);
                Object ep = handle.invoke(p);
                Object ce = ep.getClass().getMethod("getBukkitEntity", (Class<?>[]) null).invoke(ep);
                Method m = ce.getClass().getMethod("canSee", Player.class);

                boolean canSee = (boolean) m.invoke(ce, user);
                if (canSee) {
                    Constructor<?> constructor = Objects.requireNonNull(Reflections.getNMSClass("PacketPlayOutPlayerInfo")).getConstructors()[1];

                    Class<?> clazz = Reflections.getNMSClass("PacketPlayOutPlayerInfo");
                    Class<?> enumClass = clazz.getDeclaredClasses()[2];
                    Enum<?>[] enumConstants = (Enum<?>[]) enumClass.getEnumConstants();

                    Enum<?> updateDisplayType = enumConstants[3];


                    Object packet = constructor.newInstance(updateDisplayType, ep);
                    this.sendPacket(p, packet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    /**
     * CLears the Players
     * Tab Style
     * >> Name will be white
     * @param user
     * @param priority
     * @param players
     */
    private void clearTabStyle(Player user, Integer priority, List<Player> players) {
        try {
            String team_name = priority + user.getName();
            if (team_name.length() > 16)
                team_name = team_name.substring(0, 16);

            Constructor<?> constructor = Reflections.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor();
            Object packet = constructor.newInstance();

            List<String> contents = new ArrayList<>();
            contents.add(priority + user.getName());
            try {
                Reflections.setField(packet, "a", team_name);
                Reflections.setField(packet, "b", team_name);
                Reflections.setField(packet, "e", "ALWAYS");
                Reflections.setField(packet, "h", 1);
                Reflections.setField(packet, "g", contents);
            } catch (Exception ex) {
                Reflections.setField(packet, "a", team_name);
                Reflections.setField(packet, "b", team_name);
                Reflections.setField(packet, "e", "ALWAYS");
                Reflections.setField(packet, "i", 1);
                Reflections.setField(packet, "h", contents);
            }
            if (players == null) {
                Bukkit.getOnlinePlayers().forEach(t -> this.sendPacket(t, packet));
            } else {
                players.forEach(t -> this.sendPacket(t, packet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the Nametag Packet
     * to the Player
     * @param to the player to send the packet to
     * @param packet the packet
     */
    private void sendPacket(Player to, Object packet) {
        try {
            Object playerHandle = to.getClass().getMethod("getHandle", new Class[0]).invoke(to);
            Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
            playerConnection.getClass().getMethod("sendPacket", new Class[] { Reflections.getNMSClass("Packet") }).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

