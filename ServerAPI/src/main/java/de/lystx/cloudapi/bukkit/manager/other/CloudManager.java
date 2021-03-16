package de.lystx.cloudapi.bukkit.manager.other;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.utils.Reflections;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceStateChange;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import de.lystx.cloudsystem.library.enums.ServiceState;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

@Getter @Setter
public class CloudManager {

    private final CloudAPI cloudAPI;

    private ServiceState serviceState;
    private int maxPlayers;
    private String motd;

    public CloudManager(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        try {
            this.serviceState = cloudAPI.getService().getServiceState();
        } catch (NullPointerException e) {
            //Exception thrown by PaperSpigot
            this.serviceState = ServiceState.LOBBY;
        }
        this.maxPlayers = Bukkit.getMaxPlayers();
        this.motd = Bukkit.getMotd();
    }

    public void update() {
        try {
            Object playerlist = Reflections.getCraftBukkitClass("CraftServer").getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
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
        try {
            VsonObject vsonObject = new VsonObject(new File("./CLOUD/connection.json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            vsonObject.append("serviceState", this.serviceState);
            vsonObject.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CloudAPI.getInstance().sendPacket(new PacketInServiceUpdate(CloudAPI.getInstance().getService()));


        this.cloudAPI.sendPacket(new PacketInServiceStateChange(cloudAPI.getService(), this.serviceState));
    }


}
