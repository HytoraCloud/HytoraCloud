package de.lystx.cloudapi.bukkit;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.command.ServiceCommand;
import de.lystx.cloudapi.bukkit.events.other.BukkitEventEvent;
import de.lystx.cloudapi.bukkit.handler.*;
import de.lystx.cloudapi.bukkit.listener.*;
import de.lystx.cloudapi.bukkit.manager.labymod.LabyMod;
import de.lystx.cloudapi.bukkit.manager.nametag.NametagManager;
import de.lystx.cloudapi.bukkit.manager.npc.NPCManager;
import de.lystx.cloudapi.bukkit.manager.npc.impl.SkinFetcher;
import de.lystx.cloudapi.bukkit.manager.sign.SignManager;
import de.lystx.cloudapi.bukkit.manager.other.CloudManager;
import de.lystx.cloudapi.bukkit.utils.CloudPermissibleBase;
import de.lystx.cloudapi.bukkit.utils.Reflections;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class CloudServer extends JavaPlugin {

    @Getter
    private static CloudServer instance;
    private CloudAPI cloudAPI;

    private CloudManager manager;
    private SignManager signManager;
    private NametagManager nametagManager;
    private NPCManager npcManager;
    private SkinFetcher skinFetcher;
    private LabyMod labyMod;
    private boolean newVersion;
    private boolean waitingForPlayer;


    @Override
    public void onEnable() {
        instance = this;

        //Initializing Objects
        this.cloudAPI = new CloudAPI();
        this.manager = new CloudManager(this.cloudAPI);
        this.signManager = new SignManager(this);
        this.nametagManager = new NametagManager();
        this.skinFetcher = new SkinFetcher();
        this.npcManager = new NPCManager();

        // Version check to enable an disable LabyMod and NPCs
        try {
            Class.forName("net.minecraft.server.v1_8_R3.Packet");
            this.npcManager = new NPCManager();
            this.newVersion = false;
            this.labyMod = new LabyMod(this.cloudAPI);
        } catch (Exception e){
            this.newVersion = true;
        }

        // Registering PacketHandlers
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitStop(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitSignSystem(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitServerUpdate(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitCloudPlayerHandler(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitSubChannel(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitNPCs(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerTPS(this.cloudAPI));

        // Connecting to cloud and managing cloud stuff
        this.cloudAPI.getCloudClient().registerHandler(new CloudListener());

        if (this.cloudAPI.getProperties().has("waitingForPlayers")) {
            this.waitingForPlayer = true;
            this.cloudAPI.getScheduler().scheduleDelayedTask(() -> {
                if (waitingForPlayer) {
                    this.shutdown();
                }
            }, 1500L);
        }

        //Registering Events
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new NPCListener(), this);
        this.getServer().getPluginManager().registerEvents(new CommandListener(), this);

        // Registering commands
        this.cloudAPI.registerCommand(new ServiceCommand());

        //Checking for fired events
        for (HandlerList handler : HandlerList.getHandlerLists()) {
            handler.register(new RegisteredListener(new EmptyListener(), (listener, event) -> {
                if (event.getClass().getSimpleName().equalsIgnoreCase(BukkitEventEvent.class.getSimpleName())) {
                    return;
                }
                Bukkit.getPluginManager().callEvent(new BukkitEventEvent(event));
            }, EventPriority.NORMAL, this, false));
        }
    }

    @Override
    public void onDisable() {
        if (this.cloudAPI.getCloudClient().isConnected()) {
            this.cloudAPI.disconnect();
        }
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "LABYMOD");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "LMC");
        int animationScheduler = this.signManager.getSignUpdater().getAnimationScheduler();
        Bukkit.getScheduler().cancelTask(animationScheduler);

    }

    public void executeCommand(String command) {
        Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    public void shutdown() {
        String msg = this.cloudAPI.getNetworkConfig().getMessageConfig().getServerShutdownMessage().replace("&", "§").replace("%prefix%", this.cloudAPI.getPrefix());
        List<Player> list = new LinkedList<>(Bukkit.getOnlinePlayers());
        for (Player onlinePlayer : list) {
            CloudPlayer player = cloudAPI.getCloudPlayers().get(onlinePlayer.getName());
            if (player != null) {
                onlinePlayer.sendMessage(msg);
                player.fallback();
            } else {
                onlinePlayer.kickPlayer(msg);
            }

            list.remove(onlinePlayer);
            if (list.isEmpty()) {

                CloudAPI.getInstance().getScheduler().scheduleDelayedTask(() -> CloudAPI.getInstance().shutdown(), 5L);
                CloudAPI.getInstance().getScheduler().scheduleDelayedTask(Bukkit::shutdown, 7L);
            }
        }
    }

    public void updatePermissions(Player player) {
        if (!cloudAPI.getPermissionPool().isEnabled()) {
            return;
        }
        try {
            Class<?> clazz = Reflections.getCraftBukkitClass("entity.CraftHumanEntity");
            Field field = clazz.getDeclaredField("perm");
            field.setAccessible(true);
            field.set(player, new CloudPermissibleBase(player));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
