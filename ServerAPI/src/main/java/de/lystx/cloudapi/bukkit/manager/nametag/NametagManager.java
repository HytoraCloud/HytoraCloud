package de.lystx.cloudapi.bukkit.manager.nametag;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;

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
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            List<String> contents = new ArrayList<>();
            contents.add(user.getName());
            try {
                setField(packet, "a", team_name);
                setField(packet, "b", team_name);
                setField(packet, "c", prefix);
                setField(packet, "d", suffix);
                setField(packet, "e", "ALWAYS");
                setField(packet, "h", 0);
                setField(packet, "g", contents);
            } catch (Exception ex) {
                setField(packet, "a", team_name);
                setField(packet, "b", team_name);
                setField(packet, "c", prefix);
                setField(packet, "d", suffix);
                setField(packet, "e", "ALWAYS");
                setField(packet, "i", 0);
                setField(packet, "h", contents);
            }
            if (players == null) {
                for (Player t : Bukkit.getOnlinePlayers())
                    ((CraftPlayer)t).getHandle().playerConnection.sendPacket(packet);
            } else {
                for (Player p : players) {
                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setPlayerListName(Player user, String name, List<Player> ps) {
        if (ps == null) {
            ps = Lists.newLinkedList();
            ps.addAll(Bukkit.getOnlinePlayers());
        }
        CraftPlayer cp = (CraftPlayer) user.getPlayer();
        (cp.getHandle()).listName = name.equals(user.getName()) ? null : CraftChatMessage.fromString(name)[0];
        for (Player p : ps) {
            EntityPlayer ep = ((CraftPlayer) p).getHandle();
            if (ep.getBukkitEntity().canSee(user.getPlayer()))
                ep.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, cp.getHandle()));
        }
    }


    private void clearTabStyle(Player user, Integer priority, List<Player> players) {
        try {
            String team_name = priority + user.getName();
            if (team_name.length() > 16)
                team_name = team_name.substring(0, 16);
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

            List<String> contents = new ArrayList<>();
            contents.add(priority + user.getName());
            try {
                setField(packet, "a", team_name);
                setField(packet, "b", team_name);
                setField(packet, "e", "ALWAYS");
                setField(packet, "h", 1);
                setField(packet, "g", contents);
            } catch (Exception ex) {
                setField(packet, "a", team_name);
                setField(packet, "b", team_name);
                setField(packet, "e", "ALWAYS");
                setField(packet, "i", 1);
                setField(packet, "h", contents);
            }
            if (players == null) {
                for (Player t : Bukkit.getOnlinePlayers())
                    ((CraftPlayer)t).getHandle().playerConnection.sendPacket(packet);
            } else {
                for (Player p : players) {
                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setField(Object change, String name, Object to) throws Exception {
        Field field = change.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(change, to);
        field.setAccessible(false);
    }
}

