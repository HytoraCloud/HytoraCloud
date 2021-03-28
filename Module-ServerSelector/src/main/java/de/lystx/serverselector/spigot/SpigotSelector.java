package de.lystx.serverselector.spigot;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.serverselector.spigot.handler.*;
import de.lystx.serverselector.spigot.listener.NPCListener;
import de.lystx.serverselector.spigot.listener.PlayerJoinListener;
import de.lystx.serverselector.spigot.listener.PlayerQuitListener;
import de.lystx.serverselector.spigot.listener.PlayerSignListener;
import de.lystx.serverselector.spigot.manager.npc.NPCManager;
import de.lystx.serverselector.spigot.manager.npc.impl.PacketReader;
import de.lystx.serverselector.spigot.manager.npc.impl.SkinFetcher;
import de.lystx.serverselector.spigot.manager.sign.SignManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class SpigotSelector extends JavaPlugin {

    private SignManager signManager;
    private NPCManager npcManager;
    private SkinFetcher skinFetcher;

    @Getter
    private static SpigotSelector instance;

    public static final Map<UUID, PacketReader> PACKET_READERS = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        this.signManager = new SignManager();

        PluginManager pm = Bukkit.getPluginManager();

        if (!CloudAPI.getInstance().isNewVersion()) {
            this.npcManager = new NPCManager();
            this.skinFetcher = new SkinFetcher();
            pm.registerEvents(new NPCListener(), this);
        }

        CloudAPI.getInstance().registerPacketHandler(new PacketHandlerBukkitSignSystem());
        CloudAPI.getInstance().registerPacketHandler(new PacketHandlerManageSigns());
        CloudAPI.getInstance().registerPacketHandler(new PacketHandlerManageNPCs());
        CloudAPI.getInstance().registerPacketHandler(new PacketHandlerUpdate());

        pm.registerEvents(new PlayerSignListener(), this);
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new PlayerQuitListener(), this);
        System.out.println("[ServerSelector] ServerSelector-Module-Bukkit loaded up and started!");
    }

    @Override
    public void onDisable() {
        try {
            int animationScheduler = this.signManager.getSignUpdater().getAnimationScheduler();
            Bukkit.getScheduler().cancelTask(animationScheduler);
        } catch (NullPointerException e) {
            System.out.println("[ServerSelector] Couldn't cancel task for SignUpdater!");
        }
    }
}
