package de.lystx.hytoracloud.bridge.spigot.bukkit;

import de.lystx.hytoracloud.driver.commons.interfaces.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.command.ServiceCommand;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.command.StopCommand;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.bridge.spigot.bukkit.utils.DefaultBukkit;
import de.lystx.hytoracloud.bridge.spigot.bukkit.utils.NametagManager;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler.*;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.listener.PlayerChatListener;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.listener.PlayerJoinListener;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.listener.PlayerQuitListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.commons.minecraft.other.NetworkInfo;
import de.lystx.hytoracloud.driver.utils.reflection.Reflections;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class BukkitBridge extends JavaPlugin implements BridgeInstance {

    /**
     * The instance
     */
    @Getter
    private static BukkitBridge instance;

    /**
     * The nametag manager
     */
    private NametagManager nametagManager;

    /**
     * The manager to manage signs and npc
     */
    private ServerSelector serverSelector;

    /**
     * the scheduler id for the waiting
     * task to cancel it
     */
    private int taskId;

    @Override
    public void onEnable() {
        instance = this;

        CloudBridge.load(this);

        if (CloudDriver.getInstance().getConnection() == null || !CloudDriver.getInstance().getConnection().isAvailable()) {
            CloudBridge.load(this);
        }


        CloudDriver.getInstance().setInstance("bukkit", new DefaultBukkit());
        CloudDriver.getInstance().getBukkit().setVersion(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);

        //Initializing Objects
        this.serverSelector = new ServerSelector(this);
        this.nametagManager = new NametagManager();
        this.taskId = -1;

        this.bootstrap();

    }



    @Override
    public void onDisable() {
        this.serverSelector.shutdown();

        try {
            CloudDriver.getInstance().getConnection().close();
        } catch (IOException e) {
            e.printStackTrace();
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
    @Override
    public void bootstrap() {
        // Registering PacketHandlers
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerInventory());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerShutdown());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerGroupUpdate());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerCloudPlayer());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerTPS());

        //Registering Events
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        // Registering commands
        CloudDriver.getInstance().registerCommand(new ServiceCommand());
        CloudDriver.getInstance().registerCommand(new StopCommand());

        //Start checking for players or stop server
        CloudDriver.getInstance().executeIf(this::startStopTimer, () -> CloudDriver.getInstance().getCurrentService() != null);

    }

    @Override
    public void flushCommand(String command) {
        if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("bukkit:stop")) {
            this.shutdown();
        } else {
            Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        }
    }

    @Override
    public PropertyObject requestProperties() {

        IService service = CloudDriver.getInstance().getCurrentService();

        PropertyObject propertyObject = new PropertyObject();


        propertyObject.append("bukkit",
                new PropertyObject()
                    .append("version", Bukkit.getVersion())
                    .append("outgoing", Bukkit.getMessenger().getOutgoingChannels())
                    .append("incoming", Bukkit.getMessenger().getIncomingChannels())
                    .append("whitelisted", Bukkit.getWhitelistedPlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList()))
                    .append("end", Bukkit.getAllowEnd())
                    .append("nether", Bukkit.getAllowNether())
        );

        propertyObject.append("service",
                new PropertyObject()
                    .append("motd", service.getMotd())
                    .append("state", service.getState())
                    .append("max-players", service.getGroup().getMaxPlayers())
                    .append("players", service.getPlayers().size())
                    .append("online-mode", Bukkit.getOnlineMode())
        );

        List<PropertyObject> plugins = new LinkedList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {

            plugins.add(new PropertyObject()
                    .append("name", plugin.getName())
                    .append("version", plugin.getDescription().getVersion())
                    .append("authors", plugin.getDescription().getAuthors())
                    .append("dependencies", plugin.getDescription().getDepend())
                    .append("soft-dependencies", plugin.getDescription().getSoftDepend())
                    .append("description", plugin.getDescription().getDescription())
                    .append("commands", plugin.getDescription().getCommands() == null ? new LinkedList<>() : plugin.getDescription().getCommands().keySet())
                    .append("website", plugin.getDescription().getWebsite() == null ? "None" : plugin.getDescription().getWebsite())
                    .append("main-class", plugin.getDescription().getMain())
            );
        }

        propertyObject.append("plugins", plugins);

        return propertyObject;
    }

    @Override
    public String loadTPS() {
        String tps = "§c???";
        double[] arrayOfDouble = (double[]) Reflections.getField("recentTps","MinecraftServer","getServer");
        if (arrayOfDouble != null) {
            byte b = 0;
            if (b < arrayOfDouble.length) {
                tps = new NetworkInfo().formatTps(arrayOfDouble[b]);
            } else {
                tps = "§cError";
            }
        };
        return tps;
    }

    @Override
    public void shutdown() {

        if (this.taskId != -1) {
            CloudDriver.getInstance().getScheduler().cancelTask(this.taskId);
        }

        String msg = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getBukkitShutdown().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix());

        //Already no one online anymore
        if (Bukkit.getOnlinePlayers().size() <= 0) {
            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> CloudDriver.getInstance().shutdownDriver(), 5L);
            return;
        }

        //TODO: Nach 2x /stop kommt man nd mehr drauf
        Utils.doUntilEmpty(new LinkedList<>(Bukkit.getOnlinePlayers()), player -> player.kickPlayer(msg), players -> CloudDriver.getInstance().shutdownDriver());
    }

    /*
     * Starts counting to 5 Minutes
     * If no player joins within the given time
     * The server will stop due to no
     * player being online
     */
    public void startStopTimer() {
        try {
            if (!CloudDriver.getInstance().getCurrentService().getProperties().has("waitingForPlayers")) {
                return;
            }
            this.taskId = CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
                if (Bukkit.getOnlinePlayers().size() <= 0) {
                    this.shutdown();
                }
            }, 6000L).getId();
        } catch (NullPointerException e) {
            //NullPointerException when executing '/stop'
        }
    }

}
