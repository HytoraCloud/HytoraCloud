package de.lystx.hytoracloud.module.serverselector.spigot;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.module.serverselector.spigot.handler.PacketHandlerBukkitSignSystem;
import de.lystx.hytoracloud.module.serverselector.spigot.handler.PacketHandlerManageNPCs;
import de.lystx.hytoracloud.module.serverselector.spigot.handler.PacketHandlerManageSigns;
import de.lystx.hytoracloud.module.serverselector.spigot.handler.PacketHandlerUpdate;
import de.lystx.hytoracloud.module.serverselector.spigot.listener.NPCListener;
import de.lystx.hytoracloud.module.serverselector.spigot.listener.PlayerJoinListener;
import de.lystx.hytoracloud.module.serverselector.spigot.listener.PlayerQuitListener;
import de.lystx.hytoracloud.module.serverselector.spigot.listener.PlayerSignListener;
import de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.NPCManager;
import de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.impl.NPC;
import de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.impl.PacketReader;
import de.lystx.hytoracloud.module.serverselector.spigot.manager.sign.SignManager;
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
    private NPC.SkinFetcher skinFetcher;

    @Getter
    private static SpigotSelector instance;

    public static final Map<UUID, PacketReader> PACKET_READERS = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        this.signManager = new SignManager();

        PluginManager pm = Bukkit.getPluginManager();

        CloudDriver.getInstance().executeIf(() -> {
            if (!CloudDriver.getInstance().getBukkit().isNewVersion()) {
                this.npcManager = new NPCManager();
                this.skinFetcher = new NPC.SkinFetcher();
                pm.registerEvents(new NPCListener(), this);
            }

            CloudDriver.getInstance().registerPacketHandler(new PacketHandlerBukkitSignSystem());
            CloudDriver.getInstance().registerPacketHandler(new PacketHandlerManageSigns());
            CloudDriver.getInstance().registerPacketHandler(new PacketHandlerManageNPCs());
            CloudDriver.getInstance().registerPacketHandler(new PacketHandlerUpdate());

            pm.registerEvents(new PlayerSignListener(), this);
            pm.registerEvents(new PlayerJoinListener(), this);
            pm.registerEvents(new PlayerQuitListener(), this);
            System.out.println("[ServerSelector] ServerSelector-Module-Bukkit loaded up and started!");
        }, () -> CloudDriver.getInstance().getBukkit() != null);

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
