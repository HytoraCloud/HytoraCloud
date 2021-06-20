package de.lystx.hytoracloud.bridge.bukkit.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.enums.ServiceState;
import de.lystx.hytoracloud.driver.service.other.IBukkit;
import de.lystx.hytoracloud.driver.service.util.reflection.Reflections;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

@Getter @Setter
public class DefaultBukkit implements IBukkit {


    private ServiceState serviceState;
    private int maxPlayers;
    private String motd;
    private String version;

    /**
     * Checks for higher Versions
     * @return
     */
    public boolean isNewVersion() {
        if (version == null) {
            return false;
        }
        return !version.startsWith("v1_8");
    }

    /**
     * Sets the fields to defaults
     * using {@link Bukkit#getMaxPlayers()} etc
     * Tries to set {@link ServiceState} for PaperSpigot
     * and normal Spigot (bit struggling)
     *
     */
    public DefaultBukkit() {
        try {
            this.serviceState = CloudDriver.getInstance().getThisService().getServiceState();
        } catch (NullPointerException e) {
            //Exception thrown by PaperSpigot
            this.serviceState = ServiceState.LOBBY;
        }
        this.maxPlayers = Bukkit.getMaxPlayers();
        this.motd = Bukkit.getMotd();
    }

    /**
     * Method to update all Fields of a CloudService
     * Updates {@link ServiceState}, Motd, Maximum Players
     */
    public void update() {
        try {
            Object playerlist = de.lystx.hytoracloud.driver.service.util.reflection.Reflections.getCraftBukkitClass("CraftServer").getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
            Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
            maxplayers.setAccessible(true);
            maxplayers.set(playerlist, this.maxPlayers);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        Object minecraftserver;
        try {
            minecraftserver = Reflections.getCraftBukkitClass("CraftServer").getDeclaredMethod("getServer", null).invoke(Bukkit.getServer(), null);
            Field f = minecraftserver.getClass().getSuperclass().getDeclaredField("motd");
            f.setAccessible(true);
            f.set(minecraftserver, this.motd);
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException| NoSuchFieldException e) {
            e.printStackTrace();
        }
        CloudDriver.getInstance().getThisService().setServiceState(serviceState);
        CloudDriver.getInstance().getThisService().update();

    }
}
