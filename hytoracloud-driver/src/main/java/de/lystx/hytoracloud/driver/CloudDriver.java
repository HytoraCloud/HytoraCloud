package de.lystx.hytoracloud.driver;

import ch.qos.logback.classic.LoggerContext;
import de.lystx.hytoracloud.driver.commons.interfaces.*;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.utils.utillity.ReceiverInfo;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketCallEvent;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCopyTemplate;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCreateTemplate;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketLogMessage;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommand;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestModules;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.service.IEventService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.DefaultServiceRegistry;
import de.lystx.hytoracloud.driver.cloudservices.global.main.IServiceRegistry;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.fallback.Fallback;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabaseManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.Module;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.ModuleInfo;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.ModuleService;
import de.lystx.hytoracloud.driver.cloudservices.other.IBukkit;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.IServiceManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.TemplateService;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.utils.log.Loggers;
import de.lystx.hytoracloud.driver.commons.minecraft.other.TicksPerSecond;
import de.lystx.hytoracloud.driver.utils.reflection.Reflections;
import de.lystx.hytoracloud.driver.utils.utillity.CloudRunnable;
import de.lystx.hytoracloud.driver.utils.utillity.CloudMap;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.MessageConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.service.DefaultEventService;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.lib.LibraryService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;


public class CloudDriver {

    /**
     *
     * @return current version of cloud
     */
    public String getVersion() {
        return "STABLE-1.8";
    }

    /**
     * The instance of this Driver
     */
    @Getter
    private static CloudDriver instance;

    /**
     * The DriverInstance
     */
    @Getter
    private DriverParent parent;

    /**
     * The proxy bridge instance
     */
    @Setter @Getter
    private ProxyBridge proxyBridge;

    /**
     * The bridge instance
     */
    @Setter @Getter
    private BridgeInstance bridgeInstance;

    /**
     * Custom values to not create extra getters
     */
    @Getter
    private final CloudMap<String, Object> implementedData;

    /**
     * The libraryService to install MavenLibraries
     */
    @Getter
    private final LibraryService libraryService;

    /**
     * The util to get the ticks per second (TPS)
     * This is not 100% accurate
     */
    @Getter
    private final TicksPerSecond ticksPerSecond;

    /**
     * The current CloudType
     * CloudAPI or Cloud etc
     */
    @Getter
    private final CloudType driverType;

    /**
     * The current Executor for sending
     * Packets and Queries
     */
    @Getter
    private HytoraConnection connection;

    /**
     * The PermissionPool
     */
    @Setter @Getter
    private PermissionPool permissionPool;

    /**
     * THe CloudInventories of the CloudPlayers
     */
    @Getter
    private final Map<UUID, CloudPlayerInventory> cloudInventories;

    /**
     * If alle the dependencies are fully installed
     */
    @Getter
    private final boolean needsDependencies, jlineCompleterInstalled;

    /**
     * The networkHandlers
     */
    @Getter
    private final List<NetworkHandler> networkHandlers;

    /**
     * Provided BUkkit Features
     */
    @Getter
    private IBukkit bukkit;

    /**
     * To manage the players
     */
    @Getter
    private ICloudPlayerManager playerManager;

    /**
     * To manage all the databases
     * that are default provided by HytoraCloud
     */
    @Getter
    private IDatabaseManager databaseManager;

    /**
     * To manage all services
     */
    @Getter
    private IServiceManager serviceManager;

    /**
     * Manages the cloudServices
     */
    @Getter
    private final IServiceRegistry serviceRegistry;

    /**
     * TO manage all events
     */
    @Getter
    private final IEventService eventService;

    /**
     * The current network config
     */
    @Getter @Setter
    private NetworkConfig networkConfig;

    /**
     * Used to execute tasks
     */
    @Getter
    private final ExecutorService executorService = Executors.newCachedThreadPool(runnable -> {

        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        Thread thread = threadFactory.newThread(runnable);
        thread.setName(String.format(Locale.ROOT, "PoolThread-%d", ThreadLocalRandom.current().nextInt(99999)));
        thread.setUncaughtExceptionHandler((thread1, e) -> {
            if (thread1 != null && !thread1.isInterrupted()) {
                thread1.interrupt();
            }
        });
        thread.setDaemon(true);
        return thread;
    });


    /**
     * Loads the drivier and links to method
     * {@link CloudDriver#CloudDriver(CloudType)}
     *
     * @param cloudType the type of this instance
     */
    public static void loadDriver(CloudType cloudType) {
        instance = new CloudDriver(cloudType);
    }

    /**
     * Initialises the Driver with a Type
     *
     * @param driverType the type
     */
    public CloudDriver(CloudType driverType) {
        instance = this;

        this.serviceRegistry = new DefaultServiceRegistry();
        this.eventService = new DefaultEventService();
        this.driverType = driverType;


        this.cloudInventories = new HashMap<>();
        this.networkHandlers = new LinkedList<>();
        this.implementedData = new CloudMap<>();

        this.permissionPool = new PermissionPool();
        this.networkConfig = NetworkConfig.defaultConfig();

        CloudDriver.getInstance().getServiceRegistry().registerService(new FileService());
        FileService instance = this.getInstance(FileService.class);

        //Check for libraries and colored console
        if (driverType.equals(CloudType.RECEIVER) || driverType.equals(CloudType.CLOUDSYSTEM) || driverType.equals(CloudType.NONE)) {
            this.libraryService = new LibraryService(instance.getLibraryDirectory(), ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? ClassLoader.getSystemClassLoader() : null);
            this.libraryService.installDefaultLibraries();
            AnsiConsole.systemInstall();

            //Disable netty and mongoDB logging
            Loggers loggers = new Loggers((LoggerContext) LoggerFactory.getILoggerFactory(), new String[]{"io.netty", "org.mongodb.driver"});
            loggers.disable();
        } else {
            this.libraryService = new LibraryService(new File("../../../../../global/libs/"), ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? ClassLoader.getSystemClassLoader() : null);
            this.libraryService.installDefaultLibraries();
        }

        //Register Default-Services
        CloudDriver.getInstance().getServiceRegistry().registerService(new Scheduler());
        CloudDriver.getInstance().getServiceRegistry().registerService(new DefaultEventService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new CommandService());

        //Register extra features
        this.ticksPerSecond = new TicksPerSecond();
        this.serviceRegistry.registerService(new CommandService());

        //Check for dependencies
        this.needsDependencies = !Utils.existsClass("jline.console.ConsoleReader");
        this.jlineCompleterInstalled = !Utils.existsClass("jline.console.completer.Completer");


    }

    /*
     * ======================================
     *    Command and EventManaging
     * ======================================
     */

    /**
     * Registers a Command
     *
     * @param commandObject the command
     */
    public void registerCommand(Object commandObject) {
        this.getInstance(CommandService.class).registerCommand(commandObject);
    }

    /**
     * Unregisters a Command
     *
     * @param commandObject the command
     */
    public void unregisterCommand(Object commandObject) {
        this.getInstance(CommandService.class).unregisterCommand(commandObject);
    }

    /**
     * Calls an Event with the
     *
     * @param cloudEvent the event to call
     */
    public boolean callEvent(CloudEvent cloudEvent) {
        if (this.isBridge()) {
            if (this.connection != null) {
                this.connection.sendPacket(new PacketCallEvent(cloudEvent, this.getCurrentService().getName()));
            }
        } else {
            if (this.connection != null) {
                this.connection.sendPacket(new PacketCallEvent(cloudEvent, "cloud"));
            }
        }
        return this.eventService.callEvent(cloudEvent);
    }

    /*
     * ======================================
     *         Service Managing
     * ======================================
     */

    /**
     * This Method iterates through all registered
     * {@link ICloudService}s and checks if the given class
     * matches the parameter class
     *
     * @param tClass the class to get the service of
     * @return Service searched by class
     */
    public <T extends ICloudService> T getInstance(Class<T> tClass) {
        return this.serviceRegistry.getInstance(tClass);
    }

    /*
     * ======================================
     * Communication between Client and CloudSystem
     * ======================================
     */

    /**
     * Sends a message to the Cloud
     * @param prefix > Prefix of the action | Will look like this -> [PREFIX]
     * @param message > The message after the prefix
     * @param showUpInConsole > If false it will only be logged
     */
    public void messageCloud(String prefix, String message, boolean showUpInConsole) {
        this.sendPacket(new PacketLogMessage(prefix, message, showUpInConsole));
    }

    /**
     * Sends a message to the cloudSystem
     *
     * @param prefix > Prefix of action
     * @param message > The message after prefix
     */
    public void messageCloud(String prefix, Object message) {
        this.messageCloud(prefix, String.valueOf(message), true);
    }


    /*
     * ======================================
     *   Packet Managing and Network Stuff
     * ======================================
     */


    /**
     * Registers a PacketHandler
     * @param handler the handlers
     */
    public void registerPacketHandler(PacketHandler... handler) {
        for (PacketHandler o : handler) {
            connection.registerPacketHandler(o);
        }
    }

    /**
     * Registers {@link NetworkHandler}s
     * @param networkHandlers the handlers
     */
    public void registerNetworkHandler(NetworkHandler... networkHandlers) {
        this.networkHandlers.addAll(Arrays.asList(networkHandlers));
    }


    /**
     * Sends a packet to the the cloudSystem
     * Without consumer to call back
     * @param packet the packet to send
     */
    public void sendPacket(HytoraPacket packet) {
        this.sendPacket(packet,  null);
    }

    /**
     * Sends a networking {@link Component}
     *
     * @param component the component
     */
    public void sendComponent(Component component) {
        this.connection.sendComponent(component);
    }

    /**
     * Sends a packet with a consumer
     * @param packet the packet to send
     * @param consumer the consumer to accept the response
     */
    public void sendPacket(HytoraPacket packet, Consumer<Component> consumer) {
        if (consumer == null) {
            this.connection.sendPacket(packet);
        } else {

            Component reply = packet.toReply(connection);
            consumer.accept(reply);
        }
    }

    /**
     * Transfers a {@link HytoraPacket} to a {@link Component}
     * with default timeOut of 3000ms
     *
     * @param packet the packet
     * @return response
     */
    public Component getResponse(HytoraPacket packet) {
        return this.getResponse(packet, 3000);
    }

    /**
     * Transfers a {@link HytoraPacket} to a {@link Component}
     *
     * @param responsePacket the packet
     * @param timeOut the timeout
     * @return response
     */
    public Component getResponse(HytoraPacket responsePacket, int timeOut) {

        return responsePacket.toReply(connection);
    }


    /*
     * ======================================
     *         Service Managing
     * ======================================
     */

    /**
     * IF nametags should be handled by Minecraft
     */
    @Setter @Getter
    private boolean nametags;

    /**
     * If Minecraft Chat format should be used
     */
    @Setter @Getter
    private boolean useChat;

    /**
     *  Minecraft Chat format
     */
    @Setter @Getter
    private String chatFormat;

    /**
     * Returns the current {@link IService} the Driver is running on
     * might be Lobby or Proxy whatever
     *
     * @return service
     */
    public IService getCurrentService() {
        JsonEntity jsonEntity = new JsonEntity(new File("./CLOUD/HYTORA-CLOUD.json"));

        return this.serviceManager.getCachedObject(jsonEntity.getString("server"));
    }

    /**
     * Gets the Host for {@link IService}s to connect to
     *
     * @return inetAddress
     */
    public InetSocketAddress getCurrentHost() {
        if (driverType == CloudType.BRIDGE) {
            JsonEntity jsonEntity = new JsonEntity(new File("./CLOUD/HYTORA-CLOUD.json"));
            return new InetSocketAddress(jsonEntity.getString("host"), jsonEntity.getInteger("port"));
        } else if (driverType == CloudType.CLOUDSYSTEM) {
            NetworkConfig networkConfig = getInstance(ConfigService.class).getNetworkConfig();
            return new InetSocketAddress(networkConfig.getHost(), networkConfig.getPort());
        } else {
            ReceiverInfo receiverInfo = this.implementedData.getObject("receiverInfo", ReceiverInfo.class);
            return new InetSocketAddress(receiverInfo.getIpAddress(), receiverInfo.getPort());
        }
    }

    public ProxyConfig getProxyConfig() {
        NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();
        ProxyConfig proxyConfig = networkConfig.getProxyConfigs().get(CloudDriver.getInstance().getCurrentService().getGroup().getName());
        return proxyConfig == null ? ProxyConfig.defaultConfig() : proxyConfig;
    }


    /*
     * ======================================
     *         Other Methods
     * ======================================
     */


    /**
     * Gets the percent of match of two strings
     *
     * @param s1 the string to compare
     * @param s2 the string to get compared
     * @param ignoreCase if strings should be lowercased
     * @return percent as double (1.0 = 100%, 0.94 = 94%)
     */
    public double getPercentMatch(String s1, String s2, boolean ignoreCase) {
        return Utils.getPercentMatch(s1, s2, ignoreCase);
    }

    /**
     * Returns the Cloud prefix from the {@link MessageConfig}
     *
     * @return string prefix
     */
    public String getPrefix() {
        if (this.getNetworkConfig() == null) {
            return "§8[§cNullCloud§8]";
        }
        return this.getNetworkConfig().getMessageConfig().getPrefix().replace("&", "§");
    }

    /**
     * Makes the Cloud execute a command
     * @param command the command to execute
     */
    public void executeCloudCommand(String command) {
        this.sendPacket(new PacketCommand("null", command));
    }

    /**
     * Injects the CloudPermissibleBase to the Player
     *
     * @param player the player
     */
    public void updatePermissions(Object player, Object cloudPermissible) {
        if (!CloudDriver.getInstance().getPermissionPool().isEnabled()) {
            return;
        }
        try {
            Class<?> clazz = Reflections.getCraftBukkitClass("entity.CraftHumanEntity");
            Field field = clazz.getDeclaredField("perm");
            field.setAccessible(true);
            field.set(player, cloudPermissible);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns CloudProvided Scheduler
     * @return scheduler
     */
    public Scheduler getScheduler() {
        return Scheduler.getInstance();
    }

    /**
     * Executes a task ONCE when accepted
     *
     * @param runnable the task to run
     * @param request the request
     */
    public void executeIf(Runnable runnable, BooleanRequest request) {
        CloudRunnable cloudRunnable = new CloudRunnable(runnable);
        cloudRunnable.setStopAfterExecute(true);

        boolean[] b = new boolean[] {false};

        this.getScheduler().scheduleRepeatingTaskAsync(() -> {
            if (request.isAccepted()) {
                cloudRunnable.run();
                b[0] = true;
            }
        }, 1L, 1L).cancelIf(() -> b[0]);

    }

    /**
     * Returns all the Modules
     * @return list of module infos
     */
    public List<ModuleInfo> getModules() {

        if (driverType == CloudType.BRIDGE) {

            PacketRequestModules packetRequestModules = new PacketRequestModules();
            Component component = packetRequestModules.toReply(connection);

            return component.get("modules");
        } else {

            List<ModuleInfo> list = new LinkedList<>();

            if (this.getInstance(ModuleService.class) != null) {
                for (Module module : getInstance(ModuleService.class).getModules()) {
                    list.add(module.getInfo());
                }
            }

            return list;
        }
    }

    /**
     * If this instance is bridge
     * or cloudsystem or else
     *
     * @return boolean
     */
    public boolean isBridge() {
        return this.driverType == CloudType.BRIDGE;
    }

    /**
     * Sets a field of this class
     *
     * @param name the name
     * @param obj the object
     */
    public void setInstance(String name, Object obj) {
        Utils.setField(CloudDriver.class, this, name, obj);
    }

    /**
     * Returns a module by name
     * @param name of the Module
     * @return info of module
     */
    @SneakyThrows
    public ModuleInfo getModule(String name) {
        if (this.driverType == CloudType.BRIDGE) {

            List<ModuleInfo> modules = this.getModules();

            return modules
                    .stream()
                    .filter(
                            moduleInfo ->
                                    moduleInfo.getName().equalsIgnoreCase(name)
                    )
                    .findFirst()
                    .orElse(null);
        } else {
            return this.getInstance(ModuleService.class).getModule(name).getInfo();
        }
    }


    /**
     * execute something in a thread created with
     * a {@link java.util.concurrent.ThreadFactory}
     *
     * @param runnable the runnable to run
     */
    public void execute(Runnable runnable) {
        this.executorService.execute(runnable);
    }

    /**
     * Executes a task delayed but thread-safe
     *
     * @param runnable the task to run
     * @param interval the interval (e.g. 1)
     * @param timeUnit the unit (e.g. SECONDS)
     */
    public void execute(Runnable runnable, long interval, TimeUnit timeUnit) {
        this.getScheduler().scheduleDelayedTask(() -> this.execute(runnable), timeUnit.toMillis(interval));
    }


    /*
     * ======================================
     *      Template Managing
     * ======================================
     */

    /**
     * Copies a server into a specific Template
     *
     * @param IService the service to copy
     * @param template the template to copy it to
     */
    public void copyTemplate(IService IService, String template) {
        this.copyTemplate(IService, template, null);
    }

    /**
     * Copies a server into a specific Template
     * but it only copies a specific folder like "world"
     * or the "plugins" folder or "plugins/YourFolder"
     *
     * @param IService the service
     * @param template the template
     * @param specificDirectory a specific directory
     */
    public void copyTemplate(IService IService, String template, String specificDirectory) {
        if (driverType == CloudType.BRIDGE) {
            PacketInCopyTemplate packetInCopyTemplate = new PacketInCopyTemplate(IService, template, specificDirectory);
            this.sendPacket(packetInCopyTemplate);
            return;
        }
        TemplateService instance = getInstance(TemplateService.class);

        instance.copy(IService, template, specificDirectory);
    }

    /**
     * Creates a Template for a group
     *
     * @param group the group to copy
     * @param template the template
     */
    public void createTemplate(IServiceGroup group, String template) {
        if (driverType == CloudType.BRIDGE) {
            PacketInCreateTemplate packetInCreateTemplate = new PacketInCreateTemplate(group, template);
            this.sendPacket(packetInCreateTemplate);
            return;
        }
        TemplateService instance = getInstance(TemplateService.class);
        instance.createTemplate(group, template);
    }


    /*
     * ======================================
     *     Fallback Managing
     * ======================================
     */

    /**
     * Checks if player is fallback
     *
     * @param player the player
     * @return boolean
     */
    public boolean isFallback(ICloudPlayer player) {
        List<Fallback> fallbacks = this.getFallbacks(player);
        for (Fallback fallback : fallbacks) {
            if (player.getService().getGroup().getName().equalsIgnoreCase(fallback.getGroupName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns {@link IService} of
     * Fallback for {@link ICloudPlayer}
     *
     * @param player the player
     * @return fallback for player
     */
    public IService getFallback(ICloudPlayer player) {
        try {
            Fallback fallback = this.getHighestFallback(player);
            IService IService;
            try {
                IService = CloudDriver.getInstance().getServiceManager().getServices(CloudDriver.getInstance().getServiceManager().getServiceGroup(fallback.getGroupName())).get(new Random().nextInt(CloudDriver.getInstance().getServiceManager().getServices(CloudDriver.getInstance().getServiceManager().getServiceGroup(fallback.getGroupName())).size()));
            } catch (Exception e){
                IService = CloudDriver.getInstance().getServiceManager().getCachedObject(fallback.getGroupName() + "-1");
            }
            return IService;
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Gets Fallback with highest
     * ID (Example sorting 1, 2, 3)
     *
     * @param player the player
     * @return fallback
     */
    public Fallback getHighestFallback(ICloudPlayer player) {
        List<Fallback> list = this.getFallbacks(player);
        list.sort(Comparator.comparingInt(Fallback::getPriority));
        return list.get(list.size() - 1) == null ? CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback() : list.get(list.size() - 1);
    }

    /**
     * Iterates through all Fallbacks
     * if permission of fallback is null
     * or player has fallback permission
     * adds it to a list
     *
     * @param player the player
     * @return list of available fallbacks for a player
     */
    public List<Fallback> getFallbacks(ICloudPlayer player) {
        List<Fallback> list = new LinkedList<>();
        list.add(CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback());
        for (Fallback fallback : CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getFallbacks()) {
            if (CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), fallback.getPermission()) || fallback.getPermission().trim().isEmpty() || fallback.getPermission() == null) {
                list.add(fallback);
            }
        }
        return list;
    }

    /*
     * ======================================
     *         Raw extentions Methods
     * ======================================
     */

    public void shutdownDriver() {
        CloudDriver.getInstance().sendPacket(new PacketInStopServer(CloudDriver.getInstance().getCurrentService()));
        try {
            this.connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Raw method to reload
     */
    public void reload() {
    }

    /**
     * Raw method to bootstrap
     */
    public void bootstrap() {
    }

    /**
     * Raw method to shutdown
     */
    public void shutdown() {
    }

}
