package de.lystx.cloudapi.bukkit;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.command.ServiceCommand;
import de.lystx.cloudapi.bukkit.command.StopCommand;
import de.lystx.cloudapi.bukkit.events.other.BukkitEventEvent;
import de.lystx.cloudapi.bukkit.handler.*;
import de.lystx.cloudapi.bukkit.listener.cloud.CloudListener;
import de.lystx.cloudapi.bukkit.listener.player.*;
import de.lystx.cloudapi.bukkit.manager.labymod.LabyMod;
import de.lystx.cloudapi.bukkit.manager.nametag.NametagManager;
import de.lystx.cloudapi.bukkit.manager.other.CloudManager;
import de.lystx.cloudapi.bukkit.utils.CloudPermissibleBase;
import de.lystx.cloudapi.bukkit.utils.Reflections;
import de.lystx.cloudsystem.library.elements.interfaces.CloudService;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketState;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Constants;
import de.lystx.cloudsystem.library.service.util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

@Getter @Setter
public class CloudServer extends JavaPlugin implements CloudService {

    @Getter
    private static CloudServer instance;

    private CloudManager manager;
    private NametagManager nametagManager;
    private LabyMod labyMod;
    private boolean newVersion;

    private int taskId;

    @Override
    public void onEnable() {
        CloudAPI.getInstance().execute(() -> {
            instance = this;
            Constants.BUKKIT_VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

            //Initializing Objects
            this.manager = new CloudManager(CloudAPI.getInstance());
            this.nametagManager = new NametagManager();
            this.labyMod = new LabyMod(CloudAPI.getInstance());

            try {
                Class.forName("net.minecraft.server.v1_8_R3.Packet");
                this.newVersion = false;
            } catch (Exception e){
                this.newVersion = true;
            }
            CloudAPI.getInstance().setNewVersion(this.newVersion);
            this.taskId = -1;
            this.bootstrap();

        });
    }

    @Override
    public void onDisable() {
        if (CloudAPI.getInstance().getCloudClient().isConnected()) {
            CloudAPI.getInstance().getCloudClient().disconnect();
        }
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "LABYMOD");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "LMC");
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
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerInventory(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerBukkitStop(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerBukkitServerUpdate(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerBukkitCloudPlayerHandler(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerBukkitSubChannel(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerTPS(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerBukkitEvent(CloudAPI.getInstance()));

        // Connecting to cloud and managing cloud stuff
        CloudAPI.getInstance().getCloudClient().registerHandler(new CloudListener());

        //Start checking for players or stop server
        this.startStopTimer();

        //Registering Events
        this.getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        // Registering commands
        CloudAPI.getInstance().registerCommand(new ServiceCommand());
        CloudAPI.getInstance().registerCommand(new StopCommand());

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
        if (!CloudAPI.getInstance().getProperties().has("waitingForPlayers")) {
            return;
        }
        this.taskId = CloudAPI.getInstance().getScheduler().scheduleDelayedTask(() -> {
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
            CloudAPI.getInstance().getScheduler().cancelTask(this.taskId);
        }
        String msg = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getServerShutdownMessage().replace("&", "§").replace("%prefix%", CloudAPI.getInstance().getPrefix());
        int size = Bukkit.getOnlinePlayers().size();
        if (size <= 0) {
            this.shutdown0();
            return;
        }

        Utils.doUntilEmpty(new LinkedList<>(Bukkit.getOnlinePlayers()),
            player -> {
                CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(player.getName());
                if (cloudPlayer != null) {
                    player.sendMessage(msg);
                    if (CloudAPI.getInstance().getNetwork().getLobbies().size() == 1) {
                        Bukkit.getScheduler().runTask(CloudServer.getInstance(), () -> player.kickPlayer(msg));
                    } else {
                        cloudPlayer.fallback();
                    }
                } else {
                    player.kickPlayer(msg);
                }
            },
            players -> {
                this.shutdown0();
            }
        );

    }

    /**
     * Shuts down the server
     */
    private void shutdown0() {
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(() -> {
            CloudAPI.getInstance().shutdown(packetState -> {
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
        CloudAPI.getInstance().sendPacket(packet);
    }

    @Override
    public CloudExecutor getCurrentExecutor() {
        return CloudAPI.getInstance().getCurrentExecutor();
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
        if (!CloudAPI.getInstance().getPermissionPool().isEnabled()) {
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
