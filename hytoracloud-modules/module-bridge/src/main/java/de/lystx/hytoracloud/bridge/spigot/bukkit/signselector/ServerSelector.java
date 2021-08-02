package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector;

import de.lystx.hytoracloud.bridge.spigot.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.handler.BukkitHandlerSign;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.handler.BukkitHandlerSignUpdate;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.listener.*;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.NPCManager;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl.PacketReader;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.sign.SignManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStop;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerNPC;
import lombok.Getter;
import org.bukkit.Bukkit;

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
            CloudDriver.getInstance().getEventManager().registerHandler(DriverEventPlayerNPC.class, new PlayerNPCListener());
        }

        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerSign(), new BukkitHandlerSignUpdate());
        CloudDriver.getInstance().getEventManager().registerHandler(DriverEventServiceStop.class, new BukkitHandlerSign());

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
