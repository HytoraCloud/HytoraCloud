package de.lystx.hytoracloud.bridge.bukkit;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.bukkit.impl.command.ServiceCommand;
import de.lystx.hytoracloud.bridge.bukkit.impl.command.StopCommand;
import de.lystx.hytoracloud.bridge.bukkit.utils.DefaultBukkit;
import de.lystx.hytoracloud.bridge.bukkit.utils.NametagManager;
import de.lystx.hytoracloud.bridge.bukkit.impl.handler.*;
import de.lystx.hytoracloud.bridge.bukkit.impl.listener.PlayerChatListener;
import de.lystx.hytoracloud.bridge.bukkit.impl.listener.PlayerJoinListener;
import de.lystx.hytoracloud.bridge.bukkit.impl.listener.PlayerQuitListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;




import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;

@Getter @Setter
public class BukkitBridge extends JavaPlugin {

    @Getter
    private static BukkitBridge instance;

    private NametagManager nametagManager;

    private int taskId;

    @Override
    public void onEnable() {
        CloudBridge.load();

        if (CloudDriver.getInstance().getConnection() == null || !CloudDriver.getInstance().getConnection().isAvailable()) {
            CloudBridge.load();
        }
        CloudDriver.getInstance().execute(() -> {
            instance = this;

            Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "bukkit", new DefaultBukkit());

            CloudDriver.getInstance().getBukkit().setVersion(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);

            //Initializing Objects
            this.nametagManager = new NametagManager();
            this.taskId = -1;
            this.bootstrap();

        });

    }


    @Override
    public void onDisable() {
        try {
            CloudDriver.getInstance().getConnection().close();
        } catch (IOException e) {
            e.printStackTrace();
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
     * This will boot up the {@link BukkitBridge}
     * It will first register all PacketHandlers
     * Then a CloudListener for all specific CloudEvents
     * will be registered in
     * The next step it starts the stopping timer
     * And last it manages the Bukkit stuff like
     * registering {@link Listener}s, {@link org.bukkit.command.Command}s and
     * CloudCommands with the {@link Command} Annotation
     * (+ it registers the listeners to fire the  )
     */
    public void bootstrap() {
        // Registering PacketHandlers
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerInventory());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerShutdown());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerGroupUpdate());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerCloudPlayer());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerTPS());

        //Start checking for players or stop server
        this.startStopTimer();

        //Registering Events
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        // Registering commands
        CloudDriver.getInstance().registerCommand(new ServiceCommand());
        CloudDriver.getInstance().registerCommand(new StopCommand());

    }

    /*
     * Starts counting to 5 Minutes
     * If no player joins within the given time
     * The server will stop due to no
     * player being online
     */
    public void startStopTimer() {
        if (!CloudDriver.getInstance().getCurrentService().getProperties().has("waitingForPlayers")) {
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
        String msg = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServerShutdownMessage().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix());
        int size = Bukkit.getOnlinePlayers().size();
        if (size <= 0) {
            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudDriver.getInstance().shutdownDriver(), 5L);
            return;
        }

        Utils.doUntilEmpty(new LinkedList<>(Bukkit.getOnlinePlayers()),
            player -> {
                ICloudPlayer ICloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());
                if (ICloudPlayer != null) {
                    player.sendMessage(msg);
                    if (CloudDriver.getInstance().getServiceManager().getLobbies().size() == 1) {
                        Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () -> player.kickPlayer(msg));
                    } else {
                        ICloudPlayer.fallback();
                    }
                } else {
                    player.kickPlayer(msg);
                }
            },
            players -> CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudDriver.getInstance().shutdownDriver(), 5L)
        );

    }

}
