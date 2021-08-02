package de.lystx.hytoracloud.bridge.spigot.bukkit.utils;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.bridge.IBukkit;
import de.lystx.hytoracloud.driver.commons.service.ServiceInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;


@Getter @Setter
public class BukkitObject implements IBukkit {

    /**
     * The version
     */
    private String version;

    /**
     * if chat is enabled
     */
    private boolean chatSystem;

    /**
     * if nametags is enabled
     */
    private boolean nametags;

    /**
     *  Minecraft Chat format
     */
    private String chatFormat;

    /**
     * The cached motd
     */
    private String motd;

    /**
     * The cached maxPlayers
     */
    private int maxPlayers;

    @Override
    public boolean shouldUseChat() {
        return chatSystem;
    }

    @Override
    public void disableChatSystem() {
        chatSystem = false;
    }

    @Override
    public void enableChatSystem() {
        chatSystem = true;
    }

    @Override
    public boolean shouldUseNameTags() {
        return nametags;
    }

    @Override
    public void enableNameTags() {
        nametags = true;
    }

    @Override
    public void disableNameTags() {
        nametags = false;
    }

    /**
     * Checks for higher Versions
     *
     * @return boolean
     */
    public boolean isNewVersion() {
        if (version == null) {
            return false;
        }
        return !version.startsWith("v1_8");
    }

    public BukkitObject() {
        CloudDriver.getInstance().executeIf(() -> {
            this.motd = CloudDriver.getInstance().getServiceManager().getThisService().getMotd();
            this.maxPlayers = CloudDriver.getInstance().getServiceManager().getThisService().getMaxPlayers();
            this.version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        }, () -> CloudDriver.getInstance().getServiceManager().getThisService() != null);
    }

    @Override @SneakyThrows
    public void updateMotd(String motd) {
        this.motd = motd;

        Class<?> craftserver = Class.forName("org.bukkit.craftbukkit." + version + ".CraftServer");
        Class<?> dedicatedPlayerList = craftserver.getMethod("getHandle").getReturnType();
        Class<?> dedicatedServer = dedicatedPlayerList.getMethod("getServer").getReturnType();
        Object obj = dedicatedPlayerList.getMethod("getServer").invoke(craftserver.getMethod("getHandle").invoke(craftserver.cast(Bukkit.getServer())));
        dedicatedServer.getMethod("setMotd", String.class).invoke(obj, motd);


    }

    @Override
    public void updateInfo(ServiceInfo serviceInfo) {
        this.updateMotd(serviceInfo.getMotd());
        this.updateMaxPlayers(serviceInfo.getMaxPlayers());
    }

    @Override @SneakyThrows
    public void updateMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;

        String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion    + ".CraftServer").getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
        Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
        maxplayers.setAccessible(true);
        maxplayers.set(playerlist, maxPlayers);
    }

}
