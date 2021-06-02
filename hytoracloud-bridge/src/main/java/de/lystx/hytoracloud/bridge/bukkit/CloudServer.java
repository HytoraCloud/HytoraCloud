package de.lystx.hytoracloud.bridge.bukkit;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.bukkit.command.ServiceCommand;
import de.lystx.hytoracloud.bridge.bukkit.command.StopCommand;
import de.lystx.hytoracloud.bridge.bukkit.events.other.BukkitEventEvent;
//import de.lystx.bridge.bukkit.handler.*;
import de.lystx.hytoracloud.bridge.bukkit.listener.cloud.CloudListener;
//import de.lystx.bridge.bukkit.listener.player.*;
import de.lystx.hytoracloud.bridge.bukkit.manager.DefaultBukkit;
import de.lystx.hytoracloud.bridge.bukkit.manager.labymod.LabyMod;
import de.lystx.hytoracloud.bridge.bukkit.manager.nametag.NametagManager;
import de.lystx.hytoracloud.bridge.bukkit.handler.*;
import de.lystx.hytoracloud.bridge.bukkit.listener.player.PlayerChatListener;
import de.lystx.hytoracloud.bridge.bukkit.listener.player.PlayerJoinListener;
import de.lystx.hytoracloud.bridge.bukkit.listener.player.PlayerQuitListener;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import io.thunder.Thunder;
import io.thunder.connection.ErrorHandler;
import io.thunder.packet.Packet;

import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter @Setter
public class CloudServer extends JavaPlugin {

    @Getter
    private static CloudServer instance;

    private NametagManager nametagManager;
    private LabyMod labyMod;

    private int taskId;

    @Override
    public void onEnable() {

        CloudBridge.load();

        Thunder.addHandler(new ErrorHandler() {
            @Override
            public void onError(Exception e) {
                if (e.getClass().getSimpleName().equals("SocketException")) {
                    return;
                }
                e.printStackTrace();
            }

            @Override
            public void onPacketFailure(Packet packet, String s, Exception e) {
                System.out.println("[CloudAPI] A §ePacket §fcould §cnot §fbe decoded (§b" + s + "§f)");
                if (e != null) {
                    onError(e);
                }
            }
        });

        if (CloudDriver.getInstance().getConnection() == null || !CloudDriver.getInstance().getConnection().isConnected()) {
            CloudBridge.load();
        }
        CloudDriver.getInstance().execute(() -> {
            Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "bukkit", new DefaultBukkit());
            instance = this;

            try {
                Class.forName("net.minecraft.server.v1_8_R3.Packet");
                CloudDriver.getInstance().getBukkit().setVersion(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
            } catch (Exception e){
                CloudDriver.getInstance().getBukkit().setVersion(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
            }

            //Initializing Objects
            this.nametagManager = new NametagManager();
            this.labyMod = new LabyMod();

            this.taskId = -1;
            this.bootstrap();

        });

    }


    @Override
    public void onDisable() {
        if (CloudDriver.getInstance().getConnection().isConnected()) {
            CloudDriver.getInstance().getConnection().disconnect();
        }
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "LABYMOD");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "LMC");
    }

    /**
     * Executes a Command from the console
     * @param command the command to execute
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
     * will be registered in
     * The next step it starts the stopping timer
     * And last it manages the Bukkit stuff like
     * registering {@link Listener}s, {@link org.bukkit.command.Command}s and
     * CloudCommands with the {@link Command} Annotation
     * (+ it registers the listeners to fire the {@link BukkitEventEvent} )
     */
    public void bootstrap() {
        // Registering PacketHandlers
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerInventory());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerBukkitStop());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerBukkitServerUpdate());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerBukkitCloudPlayerHandler());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerTPS());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerBukkitEvent());
        CloudDriver.getInstance().registerPacketHandler(new PacketHandlerBukkitRequest());

        // Connecting to cloud and managing cloud stuff
        CloudDriver.getInstance().registerNetworkHandler(new CloudListener());

        //Start checking for players or stop server
        this.startStopTimer();

        //Registering Events
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        // Registering commands
        CloudDriver.getInstance().registerCommand(new ServiceCommand());
        CloudDriver.getInstance().registerCommand(new StopCommand());

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
        if (!CloudDriver.getInstance().getThisService().getProperties().has("waitingForPlayers")) {
            return;
        }
        this.taskId = CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
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
            CloudDriver.getInstance().getScheduler().cancelTask(this.taskId);
        }
        String msg = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServerShutdownMessage().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix());
        int size = Bukkit.getOnlinePlayers().size();
        if (size <= 0) {
            this.shutdown0();
            return;
        }

        Utils.doUntilEmpty(new LinkedList<>(Bukkit.getOnlinePlayers()),
            player -> {
                CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());
                if (cloudPlayer != null) {
                    player.sendMessage(msg);
                    if (CloudDriver.getInstance().getServiceManager().getLobbies().size() == 1) {
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
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudDriver.getInstance().shutdownDriver(), 5L);
    }

}
