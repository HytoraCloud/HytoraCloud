package de.lystx.cloudapi.bukkit;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.command.ServiceCommand;
import de.lystx.cloudapi.bukkit.command.StopCommand;
import de.lystx.cloudapi.bukkit.events.other.BukkitEventEvent;
import de.lystx.cloudapi.bukkit.handler.*;
import de.lystx.cloudapi.bukkit.listener.cloud.CloudListener;
import de.lystx.cloudapi.bukkit.listener.other.NPCListener;
import de.lystx.cloudapi.bukkit.listener.player.*;
import de.lystx.cloudapi.bukkit.manager.labymod.LabyMod;
import de.lystx.cloudapi.bukkit.manager.nametag.NametagManager;
import de.lystx.cloudapi.bukkit.manager.npc.NPCManager;
import de.lystx.cloudapi.bukkit.manager.npc.impl.PacketReader;
import de.lystx.cloudapi.bukkit.manager.npc.impl.SkinFetcher;
import de.lystx.cloudapi.bukkit.manager.sign.SignManager;
import de.lystx.cloudapi.bukkit.manager.other.CloudManager;
import de.lystx.cloudapi.bukkit.utils.CloudPermissibleBase;
import de.lystx.cloudapi.bukkit.utils.Reflections;
import de.lystx.cloudsystem.library.CloudService;
import de.lystx.cloudsystem.library.CloudType;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketState;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

@Getter @Setter
public class CloudServer extends JavaPlugin implements CloudService {

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

    private int taskId;

    public static final Map<UUID, PacketReader> PACKET_READERS = new HashMap<>();

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
        this.taskId = -1;
        this.bootstrap();
    }

    @Override
    public void onDisable() {
        if (this.cloudAPI.getCloudClient().isConnected()) {
            this.cloudAPI.disconnect();
        }
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "LABYMOD");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "LMC");
        try {
            int animationScheduler = this.signManager.getSignUpdater().getAnimationScheduler();
            Bukkit.getScheduler().cancelTask(animationScheduler);
        } catch (NullPointerException e) {
            System.out.println("[CloudAPI] Couldn't cancel task for SignUpdater!");
        }
    }

    /**
     * Executes a Command from the console
     * @param command
     */
    public void executeCommand(String command) {
        if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("bukkit:stop")) {
            this.shutdown();
        } else {
            Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        }
    }

    /**
     * This will boot up the {@link CloudServer}
     * It will first register all PacketHandlers
     * Then a CloudListener for all specific CloudEvents
     * will be registered in {@link de.lystx.cloudsystem.library.service.network.defaults.CloudClient}
     * The next step it starts the stopping timer
     * And last it manages the Bukkit stuff like
     * registering {@link Listener}s, {@link org.bukkit.command.Command}s and
     * CloudCommands with the {@link de.lystx.cloudsystem.library.service.command.base.Command} Annotation
     * (+ it registers the listeners to fire the {@link BukkitEventEvent} )
     */
    public void bootstrap() {
        // Registering PacketHandlers
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerInventory(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitStop(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitSignSystem(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitServerUpdate(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitCloudPlayerHandler(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitSubChannel(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitNPCs(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerTPS(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitEvent(this.cloudAPI));

        // Connecting to cloud and managing cloud stuff
        this.cloudAPI.getCloudClient().registerHandler(new CloudListener());

        //Start checking for players or stop server
        this.startStopTimer();

        //Registering Events
        this.getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerSignListener(), this);
        this.getServer().getPluginManager().registerEvents(new NPCListener(), this);

        // Registering commands
        this.cloudAPI.registerCommand(new ServiceCommand());
        this.cloudAPI.registerCommand(new StopCommand());

        //Checking for fired events
        for (EventPriority value : EventPriority.values()) {
            this.registerListener(value, true);
            this.registerListener(value, false);
        }
    }

    /**
     * Registers Handler for {@link BukkitEventEvent}
     * @param eventPriority
     * @param ignore
     */
    public void registerListener(EventPriority eventPriority, boolean ignore) {
        for (HandlerList handler : HandlerList.getHandlerLists()) {
            handler.register(new RegisteredListener(new Listener(){}, (listener, event) -> {
                if (event.getClass().getSimpleName().equalsIgnoreCase(BukkitEventEvent.class.getSimpleName())) {
                    return;
                }
                Bukkit.getPluginManager().callEvent(new BukkitEventEvent(event));
            }, eventPriority, this, ignore));
        }
    }

    /*
     * Starts counting to 5 Minutes
     * If no player joins within the given time
     * The server will stop due to no
     * player being online
     */
    public void startStopTimer() {
        if (!this.cloudAPI.getProperties().has("waitingForPlayers")) {
            return;
        }
        this.taskId = this.cloudAPI.getScheduler().scheduleDelayedTask(() -> {
            if (Bukkit.getOnlinePlayers().size() <= 0) {
                this.shutdown();
            }
        }, 6000L).getId();
    }

    /**
     * Iterates through all onlinePlayers
     * if a fallback server exists the player
     * will be fallbacked. If not the
     * player will just be kicked
     * If no players are remaining online
     * the CloudAPI will disconnect and the
     * BukkitServer will shut down
     */
    public void shutdown() {
        if (this.taskId != -1) {
            this.cloudAPI.getScheduler().cancelTask(this.taskId);
        }
        String msg = this.cloudAPI.getNetworkConfig().getMessageConfig().getServerShutdownMessage().replace("&", "§").replace("%prefix%", this.cloudAPI.getPrefix());
        int size = Bukkit.getOnlinePlayers().size();
        if (size <= 0) {
            this.shutdown0();
            return;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CloudPlayer player = cloudAPI.getCloudPlayers().get(onlinePlayer.getName());
            if (player != null) {
                onlinePlayer.sendMessage(msg);
                if (CloudAPI.getInstance().getNetwork().getLobbies().size() == 1) {
                    onlinePlayer.kickPlayer(msg);
                } else {
                    player.fallback();
                }
            } else {
                onlinePlayer.kickPlayer(msg);
            }
            size--;
            if (size <= 0) {
                this.shutdown0();
            }
        }
    }


    private void shutdown0() {
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(() -> {
            this.cloudAPI.shutdown(packetState -> {
                if (packetState == PacketState.SUCCESS) {
                    Bukkit.shutdown();
                } else {
                    System.out.println("[CloudAPI] PacketPlayInStopServer couldn't be send! Stopping server was cancelled!");
                }
            });
        }, 5L);
    }

    /**
     * Refers to the {@link CloudAPI#sendPacket(Packet)} Method
     * and sends the packet without consumer
     * @param packet
     */
    public void sendPacket(Packet packet) {
        this.cloudAPI.sendPacket(packet);
    }

    @Override
    public CloudExecutor getCurrentExecutor() {
        return this.cloudAPI.getCurrentExecutor();
    }

    @Override
    public CloudType getType() {
        return CloudType.CLOUDAPI;
    }

    /**
     * Injects the {@link CloudPermissibleBase} to the Player
     * @param player
     */
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
