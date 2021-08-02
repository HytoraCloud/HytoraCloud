package de.lystx.hytoracloud.bridge.spigot.bukkit.utils;

import java.lang.reflect.*;
import java.util.*;

import de.lystx.hytoracloud.driver.utils.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class NametagManager {

    /**
     * Updates the nametag for a player with given values
     *
     * @param prefix the prefix of the tablist
     * @param suffix the suffix of the tablist
     * @param priority the sorting priority
     * @param user the player name
     */
    public void setNametag(String prefix, String suffix, Integer priority, String user) {
        this.setNametag(prefix, suffix, priority, user, null);
    }

    /**
     * Sets a nametag for a player and only visible for a given
     * list of players
     *
     * @param prefix the prefix
     * @param suffix the suffix
     * @param priority the sorting priority
     * @param user the player name
     * @param players the players that see it
     */
    public void setNametag(String prefix, String suffix, Integer priority, String user, List<Player> players) {
        this.clearTabStyle(Bukkit.getPlayer(user), priority, players);
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
                Bukkit.getOnlinePlayers().forEach(t -> Reflections.sendPacket(t, packet));
            } else {
                players.forEach(t -> Reflections.sendPacket(t, packet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the tab style of a player
     *
     * @param user the player
     * @param priority the priority to unclear
     * @param players the players that see
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
                Bukkit.getOnlinePlayers().forEach(t -> Reflections.sendPacket(t, packet));
            } else {
                players.forEach(t -> Reflections.sendPacket(t, packet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

