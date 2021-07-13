package de.lystx.hytoracloud.bridge.bukkit.signselector;

import de.lystx.hytoracloud.bridge.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.bukkit.signselector.handler.BukkitHandlerSign;
import de.lystx.hytoracloud.bridge.bukkit.signselector.handler.BukkitHandlerSignUpdate;
import de.lystx.hytoracloud.bridge.bukkit.signselector.listener.*;
import de.lystx.hytoracloud.bridge.bukkit.signselector.manager.npc.NPCManager;
import de.lystx.hytoracloud.bridge.bukkit.signselector.manager.npc.impl.NPC;
import de.lystx.hytoracloud.bridge.bukkit.signselector.manager.npc.impl.PacketReader;
import de.lystx.hytoracloud.bridge.bukkit.signselector.manager.sign.SignManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ServerSelector {

    /**
     * The sign manager
     */
    private final SignManager signManager;

    /**
     * The npc manager to work with
     */
    private NPCManager npcManager;

    /**
     * The skinfetcher to fetch data
     */
    private NPC.SkinFetcher skinFetcher;

    @Getter
    private static ServerSelector instance;

    /**
     * The packet reader map cache
     */
    public static final Map<UUID, PacketReader> PACKET_READERS = new HashMap<>();

    public ServerSelector(BukkitBridge bridge) {

        instance = this;

        this.signManager = new SignManager();
        PluginManager pm = Bukkit.getPluginManager();

        if (!CloudDriver.getInstance().getBukkit().isNewVersion()) {
            this.npcManager = new NPCManager();
            this.skinFetcher = new NPC.SkinFetcher();
            pm.registerEvents(new PlayerNPCListener(), bridge);
        }

        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerSign());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerSignUpdate());

        pm.registerEvents(new PlayerSignListener(), bridge);
        pm.registerEvents(new PlayerJoinPacketListener(), bridge);
        pm.registerEvents(new PlayerQuitPacketListener(), bridge);
        System.out.println("[CloudBridge-ServerSelector] SignSystem and NPCs are fully loaded and set up!");
    }

    /**
     * Shuts down this selector
     */
    public void shutdown() {
        try {
            int animationScheduler = this.signManager.getSignUpdater().getAnimationScheduler();
            Bukkit.getScheduler().cancelTask(animationScheduler);
        } catch (NullPointerException e) {
            System.out.println("[ServerSelector] Couldn't cancel task for SignUpdater!");
        }
    }
}
