package de.lystx.cloudapi.bukkit.manager.nametag;

import java.lang.reflect.*;
import java.util.*;

import de.lystx.cloudapi.bukkit.utils.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NametagManager {
    
    private final Map<Player, String> prefixcache = Maps.newConcurrentMap();
    private final Map<Player, Integer> prioritycache = Maps.newConcurrentMap();
    private final Map<Player, String> suffixcache = Maps.newConcurrentMap();
    private final List<Player> loaded = Lists.newLinkedList();

    public void setPrefix(Player user, String prefix) {
        if (loaded.contains(user)) {
            setNametag(prefix, suffixcache.get(user), prioritycache.get(user), user);
        }
    }

    public void setPrefix(Player user, String prefix, List<Player> players) {
        if (loaded.contains(user)) {
            setNametag(prefix, suffixcache.get(user), prioritycache.get(user), user, players);
        }
    }

    public void setSuffix(Player user, String suffix) {
        if (loaded.contains(user)) {
            setNametag(prefixcache.get(user), suffix, prioritycache.get(user), user);
        }
    }

    public void setSuffix(Player user, String suffix, List<Player> players) {
        if (loaded.contains(user)) {
            setNametag(prefixcache.get(user), suffix, prioritycache.get(user), user, players);
        }
    }

    public void setNametag(String prefix, String suffix, Integer priority, Player user) {
        voidForSettingTab(prefix, suffix, priority, user, null);
    }

    public void setNametag(String prefix, String suffix, Integer priority, Player user, List<Player> players) {
        voidForSettingTab(prefix, suffix, priority, user, players);
    }

    public void clearCache() {
        suffixcache.clear();
        prefixcache.clear();
        prioritycache.clear();
        loaded.clear();
    }

    public String getSuffix(Player user) {
        String suffix = suffixcache.get(user);
        if (suffix != null) {
            return suffix;
        } else {
            throw new NullPointerException("§cNo suffix cached for player " + user.getName());
        }
    }

    public Integer getPriority(Player user) {
        Integer suffix = prioritycache.get(user);
        if (suffix != null) {
            return suffix;
        } else {
            throw new NullPointerException("§cNo priority cached for player " + user.getName());
        }
    }

    public String getPrefix(Player user) {
        String prefix = prefixcache.get(user);
        if (prefix != null) {
            return prefix;
        } else {
            throw new NullPointerException("§cNo prefix cached for player " + user.getName());
        }
    }

    private void voidForSettingTab(String prefix, String suffix, Integer priority, Player user, List<Player> players) {
        clearTabStyle(user, priority, players);
        loaded.add(user);
        String team_name = priority + user.getName();
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
            setPlayerListName(user, prefix + user.getName() + suffix, players);
            prefixcache.put(user, prefix);
            suffixcache.put(user, suffix);
            prioritycache.put(user, priority);
            Constructor<?> constructor = Reflections.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor();
            Object packet = constructor.newInstance();
            List<String> contents = new ArrayList<>();
            contents.add(user.getName());
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

