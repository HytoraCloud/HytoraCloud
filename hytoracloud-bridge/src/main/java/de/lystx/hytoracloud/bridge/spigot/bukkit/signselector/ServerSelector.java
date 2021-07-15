package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector;

import de.lystx.hytoracloud.bridge.spigot.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.handler.BukkitHandlerSign;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.handler.BukkitHandlerSignUpdate;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.listener.*;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.NPCManager;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl.NPC;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl.PacketReader;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.sign.SignManager;
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

    @Getter
    private static ServerSelector instance;

    /**
     * The packet reader map cache
     */
    public static final Map<UUID, PacketReader> PACKET_READERS = new HashMap<>();

    public ServerSelector(BukkitBridge bridge) {

        instance = this;

        this.signManager = new SignManager();

        if (!CloudDriver.getInstance().getBukkit().isNewVersion()) {
            this.npcManager = new NPCManager();
            bridge.getServer().getPluginManager().registerEvents(new PlayerNPCListener(), bridge);
        }

        CloudDriver.getInstance().registerPacketHandler(
                new BukkitHandlerSign(),
                new BukkitHandlerSignUpdate()
        );

        bridge.getServer().getPluginManager().registerEvents(new PlayerSignListener(), bridge);
        bridge.getServer().getPluginManager().registerEvents(new PlayerJoinPacketListener(), bridge);
        bridge.getServer().getPluginManager().registerEvents(new PlayerQuitPacketListener(), bridge);
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
