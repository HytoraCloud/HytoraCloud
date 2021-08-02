package de.lystx.hytoracloud.bridge.spigot.bukkit;

import de.lystx.hytoracloud.driver.bridge.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.command.ServiceCommand;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.command.StopCommand;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.bridge.spigot.bukkit.utils.BukkitObject;
import de.lystx.hytoracloud.bridge.spigot.bukkit.utils.NametagManager;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler.*;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.listener.PlayerChatListener;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.listener.PlayerJoinListener;
import de.lystx.hytoracloud.bridge.spigot.bukkit.impl.listener.PlayerQuitListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.minecraft.MinecraftInfo;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.minecraft.entity.MinecraftEntity;
import de.lystx.hytoracloud.driver.commons.minecraft.entity.MinecraftPlayer;
import de.lystx.hytoracloud.driver.commons.minecraft.plugin.PluginInfo;
import de.lystx.hytoracloud.driver.commons.minecraft.world.*;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.minecraft.other.NetworkInfo;
import de.lystx.hytoracloud.driver.commons.storage.CloudMap;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.utils.Reflections;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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


        CloudDriver.getInstance().setInstance("bukkit", new BukkitObject());
        CloudDriver.getInstance().getBukkit().setVersion(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);

        //Initializing Objects
        this.serverSelector = new ServerSelector(this);
        this.nametagManager = new NametagManager();
        this.taskId = -1;

        this.bootstrap();

    }


    /**
     * Transforms a {@link MinecraftLocation} into bukkit {@link Location}
     *
     * @param location the mc location
     * @return bukkit location
     */
    public Location fromLocation(MinecraftLocation location) {
        return new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Transforms a {@link Location} into bukkit {@link MinecraftLocation}
     *
     * @param location the bukkit location
     * @return mc location
     */
    public MinecraftLocation toLocation(Location location) {
        return new MinecraftLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
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
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerShutdown());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerGroupUpdate());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerCloudPlayer());
        CloudDriver.getInstance().registerPacketHandler(new BukkitHandlerRequest());

        //Registering Events
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        // Registering commands
        CloudDriver.getInstance().registerCommand(new ServiceCommand());
        CloudDriver.getInstance().registerCommand(new StopCommand());

        //Start checking for players or stop server
        CloudDriver.getInstance().executeIf(this::startStopTimer, () -> CloudDriver.getInstance().getServiceManager().getThisService() != null);

    }

    @Override
    public void flushCommand(String command) {
        if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("bukkit:stop")) {
            CloudDriver.getInstance().getExecutorService().execute(this::shutdown);
        } else {
            Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        }
    }

    @Override
    public PropertyObject requestProperties() {

        IService service = CloudDriver.getInstance().getServiceManager().getThisService();

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

        List<JsonObject<?>> plugins = new LinkedList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {

            plugins.add(JsonObject.serializable()
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

    /**
     * Loads a {@link MinecraftInfo} of this bukkit server
     *
     * @return info with all values
     */
    public MinecraftInfo requestMinecraft() {
        List<MinecraftWorld> worlds = new LinkedList<>();
        List<MinecraftPlayer> players = new LinkedList<>();
        List<PluginInfo> plugins = new LinkedList<>();

        for (World world : Bukkit.getWorlds()) {

            Map<String, String> gameRules = new HashMap<>();
            List<MinecraftChunk> chunks = new LinkedList<>();
            List<MinecraftEntity> entities = new LinkedList<>();

            for (String gameRule : world.getGameRules()) {
                gameRules.put(gameRule, world.getGameRuleValue(gameRule));
            }

            for (Chunk loadedChunk : world.getLoadedChunks()) {

                int x1 = loadedChunk.getX();
                int z1 = loadedChunk.getZ();

                List<MinecraftBlock> blocks = new LinkedList<>();

                int minX = loadedChunk.getX() << 4;
                int minZ = loadedChunk.getZ() << 4;
                int maxX = minX | 15;
                int maxY = loadedChunk.getWorld().getMaxHeight();
                int maxZ = minZ | 15;

                for (int x = minX; x <= maxX; ++x) {
                    for (int y = 0; y <= maxY; ++y) {
                        for (int z = minZ; z <= maxZ; ++z) {
                            
                        }
                    }
                }

                chunks.add(new MinecraftChunk(x1, z1, loadedChunk.isLoaded(), blocks));
            }

            for (Entity entity : world.getEntities()) {
                MinecraftEntity minecraftEntity;
                if (entity instanceof Player) {
                    Player player = (Player)entity;

                    minecraftEntity = new MinecraftPlayer(
                            player.getUniqueId(),
                            player.getEntityId(),
                            player.getType().name(),
                            player.getCustomName(),
                            new MinecraftLocation(
                                    world.getName(),
                                    player.getLocation().getX(),
                                    player.getLocation().getY(),
                                    player.getLocation().getZ(),
                                    player.getLocation().getYaw(),
                                    player.getLocation().getPitch()
                            ),
                            player.getName(),
                            player.getHealth(),
                            player.getFoodLevel(),
                            player.getExp()
                    );
                } else {
                    minecraftEntity = new MinecraftEntity(
                        entity.getUniqueId(),
                        entity.getEntityId(),
                        entity.getType().name(),
                        entity.getCustomName(),

                        new MinecraftLocation(
                                world.getName(),
                                entity.getLocation().getX(),
                                entity.getLocation().getY(),
                                entity.getLocation().getZ(),
                                entity.getLocation().getYaw(),
                                entity.getLocation().getPitch()
                        )
                    );
                }
                entities.add(minecraftEntity);
            }

            worlds.add(new MinecraftWorld(world.getName(),  world.getUID(), world.getDifficulty().name(), gameRules, chunks, entities));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(new MinecraftPlayer(
                            player.getUniqueId(),
                            player.getEntityId(),
                            player.getType().name(),
                            player.getCustomName(),
                            new MinecraftLocation(
                                    player.getWorld().getName(),
                                    player.getLocation().getX(),
                                    player.getLocation().getY(),
                                    player.getLocation().getZ(),
                                    player.getLocation().getYaw(),
                                    player.getLocation().getPitch()
                            ),
                            player.getName(),
                            player.getHealth(),
                            player.getFoodLevel(),
                            player.getExp()
                    )
            );
        }

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {

            plugins.add(new PluginInfo(
                    plugin.getName(),
                    plugin.getDescription().getAuthors().toArray(new String[0]),
                    plugin.getDescription().getVersion(),
                    plugin.getDescription().getMain(),
                    plugin.getDescription().getWebsite() == null ? "None" : plugin.getDescription().getWebsite(),
                    (plugin.getDescription().getCommands() == null ? new LinkedList<>() : plugin.getDescription().getCommands().keySet()).toArray(new String[0]),
                    plugin.getDescription().getDescription(),
                    plugin.getDescription().getDepend().toArray(new String[0]),
                    plugin.getDescription().getSoftDepend().toArray(new String[0]))
            );
        }

        return new MinecraftInfo(
                Bukkit.getVersion(),
                Bukkit.getMessenger().getOutgoingChannels().toArray(new String[0]),
                Bukkit.getMessenger().getIncomingChannels().toArray(new String[0]),
                plugins, worlds, players
        );
    }

    @Override
    public Map<String, Object> loadExtras() {
        return new CloudMap<String, Object>().append("info", requestMinecraft());
    }

    @Override @SneakyThrows
    public void sendTabList(UUID uniqueId, String header, String footer) {

        Player player = Bukkit.getPlayer(uniqueId);

        if (player == null) {
            return;
        }

        Class<?> chatMessageClass = Reflections.getNMSClass("ChatMessage");

        Object tablistHeader = chatMessageClass.getConstructor(String.class, Object[].class).newInstance(header, new Object[0]);
        Object tablistFooter = chatMessageClass.getConstructor(String.class, Object[].class).newInstance(footer, new Object[0]);

        Object tablist = Reflections.getNMSClass("PacketPlayOutPlayerListHeaderFooter").newInstance();
        try {
            Field headerField = tablist.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(tablist, tablistHeader);
            headerField.setAccessible(!headerField.isAccessible());
            Field footerField = tablist.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(tablist, tablistFooter);
            footerField.setAccessible(!footerField.isAccessible());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Reflections.sendPacket(player, tablist);
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
    public ServiceType type() {
        return ServiceType.SPIGOT;
    }

    @Override
    public void shutdown() {

        if (this.taskId != -1) {
            CloudDriver.getInstance().getScheduler().cancelTask(this.taskId);
        }

        String msg = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getBukkitShutdown().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix());

        //Already no one online anymore
        if (Bukkit.getOnlinePlayers().size() <= 0) {
            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(this::shutdownDriver, 5L);
            return;
        }

        int count = Bukkit.getOnlinePlayers().size();
        for (Player player : Bukkit.getOnlinePlayers()) {

            ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getName());
            if (cloudPlayer == null) {
                player.kickPlayer(msg);
            } else {
                IService fallback = CloudDriver.getInstance().getFallbackManager().getFallbackExcept(cloudPlayer, CloudDriver.getInstance().getServiceManager().getThisService());
                cloudPlayer.sendMessage(new ChatComponent(msg));
                cloudPlayer.connect(fallback);
            }

            count--;
            if (count <= 0) {
                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(this::shutdownDriver, 6L);
            }
        }

    }

    /**
     * Shuts down the driver connection
     */
    public void shutdownDriver() {
        CloudDriver.getInstance().sendPacket(new PacketInStopServer(CloudDriver.getInstance().getServiceManager().getThisService().getName()));

        try {
            CloudDriver.getInstance().getConnection().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getPing(UUID playerUniqueId) {
        Player player = Bukkit.getPlayer(playerUniqueId);
        if (player == null) {
            return -1;
        }
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
     * Starts counting to 5 Minutes
     * If no player joins within the given time
     * The server will stop due to no
     * player being online
     */
    public void startStopTimer() {
        IService currentService = CloudDriver.getInstance().getServiceManager().getThisService();
        if (currentService == null) {
            return;
        }
        try {
            if (!currentService.getProperties().has("serviceTimeOut")) {
                return;
            }
            this.taskId = CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
                if (Bukkit.getOnlinePlayers().size() <= 0) {
                    this.shutdown();
                }
            }, (currentService.getProperties().getLong("serviceTimeOut") * 20)).getId();
        } catch (NullPointerException e) {
            //NullPointerException when executing '/stop'
        }
    }

}
