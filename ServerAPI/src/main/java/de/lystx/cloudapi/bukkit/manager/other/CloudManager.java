package de.lystx.cloudapi.bukkit.manager.other;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInServiceStateChange;
import de.lystx.cloudsystem.library.enums.ServiceState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

@Getter @Setter
public class CloudManager {

    private final CloudAPI cloudAPI;

    private ServiceState serviceState;
    private int maxPlayers;
    private String motd;

    public CloudManager(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;

        this.serviceState = cloudAPI.getService().getServiceState();
        this.maxPlayers = Bukkit.getMaxPlayers();
        this.motd = Bukkit.getMotd();

    }

    public void update() {
        String bukkitversion = this.getBukkitVersion();
        try {
            Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion    + ".CraftServer").getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
            Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
            maxplayers.setAccessible(true);
            maxplayers.set(playerlist, this.maxPlayers);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        Object minecraftserver;
        try {
            minecraftserver = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer").getDeclaredMethod("getServer", null).invoke(Bukkit.getServer(), null);
            Field f = minecraftserver.getClass().getSuperclass().getDeclaredField("motd");
            f.setAccessible(true);
            f.set(minecraftserver, this.motd);
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|ClassNotFoundException|NoSuchFieldException e) {
            e.printStackTrace();
        }
        this.cloudAPI.sendPacket(new PacketPlayInServiceStateChange(cloudAPI.getService(), this.serviceState));
    }


    private String getBukkitVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

}
