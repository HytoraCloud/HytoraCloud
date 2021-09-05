package de.lystx.hytoracloud.driver;

import ch.qos.logback.classic.LoggerContext;
import de.lystx.hytoracloud.driver.connection.cloudflare.ICloudFlareManager;
import de.lystx.hytoracloud.driver.connection.cloudflare.def.DefaultCloudFlareManager;
import de.lystx.hytoracloud.driver.config.IConfigManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.event.events.network.DriverEventReload;
import de.lystx.hytoracloud.driver.service.screen.IScreenManager;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.utils.interfaces.BooleanRequest;
import de.lystx.hytoracloud.driver.utils.interfaces.DriverParent;
import de.lystx.hytoracloud.driver.service.bridge.BridgeInstance;
import de.lystx.hytoracloud.driver.utils.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.service.minecraft.DefaultMinecraftManager;
import de.lystx.hytoracloud.driver.service.minecraft.IMinecraftManager;
import de.lystx.hytoracloud.driver.service.receiver.DefaultReceiverManager;
import de.lystx.hytoracloud.driver.service.receiver.IReceiverManager;
import de.lystx.hytoracloud.driver.wrapped.SchedulerObject;
import de.lystx.hytoracloud.driver.utils.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.driver.service.util.IDService;
import de.lystx.hytoracloud.driver.service.util.PortService;
import de.lystx.hytoracloud.driver.console.color.ConsoleColor;
import de.lystx.hytoracloud.driver.module.def.DefaultModuleManager;
import de.lystx.hytoracloud.driver.module.IModuleManager;
import de.lystx.hytoracloud.driver.service.group.IGroupManager;
import de.lystx.hytoracloud.driver.connection.messenger.DefaultChannelMessenger;
import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessenger;
import de.lystx.hytoracloud.driver.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.service.fallback.DefaultFallbackManager;
import de.lystx.hytoracloud.driver.service.fallback.IFallbackManager;
import de.lystx.hytoracloud.driver.service.template.def.DefaultTemplateManager;
import de.lystx.hytoracloud.driver.service.template.ITemplateManager;
import de.lystx.hytoracloud.driver.command.ICommandManager;
import de.lystx.hytoracloud.driver.packets.both.PacketReload;
import de.lystx.hytoracloud.driver.packets.both.PacketReloadService;
import de.lystx.hytoracloud.driver.connection.protocol.requests.RequestManager;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.packets.both.PacketLogMessage;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.event.def.DefaultEventManager;
import de.lystx.hytoracloud.driver.event.IEventManager;
import de.lystx.hytoracloud.driver.registry.def.DefaultServiceRegistry;
import de.lystx.hytoracloud.driver.registry.IServiceRegistry;
import de.lystx.hytoracloud.driver.command.DefaultCommandManager;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.connection.database.IDatabaseManager;
import de.lystx.hytoracloud.driver.service.util.IBukkit;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.service.IServiceManager;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import de.lystx.hytoracloud.driver.console.logger.Loggers;
import de.lystx.hytoracloud.driver.service.minecraft.other.TicksPerSecond;
import de.lystx.hytoracloud.driver.utils.other.CloudRunnable;
import de.lystx.hytoracloud.driver.utils.other.CloudMap;
import de.lystx.hytoracloud.driver.config.impl.MessageConfig;
import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.library.LibraryService;
import de.lystx.hytoracloud.driver.utils.other.UUIDPool;
import lombok.Getter;


import org.fusesource.jansi.AnsiConsole;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.AccessibleObject;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Getter


@DriverInfo(
        version = "STABLE-1.8",
        contributors = {"Lystx", "cxt", "Ian S."},
        lowestSupportVersion = "1.8",
        highestSupportVersion = "1.16.5",
        allowedJavaVersions = {"1.8"},
        packetProtocolVersion = 44,
        todo = {
                "Java 11, Java 16 -> Velocity Support",
                "Check NPCs",
                "Fix Sign-Thread-Stop"
        }
)
public class CloudDriver {

    @Getter
    private static CloudDriver instance; //The global driver instance

    public static Optional<CloudDriver> instance() {
        return Optional.ofNullable(instance);
    }

    //Final other values
    private final CloudType driverType; //The type of this instance
    private final CloudMap<String, Object> implementedData; //Data to store to not have to create attributes
    private final LibraryService libraryService; //The libraryService to install MavenLibraries
    private final TicksPerSecond ticksPerSecond; //The util to get the ticks per second (TPS)
    private final UUIDPool mojangPool; //The current UUIDPool for uuid-name-cache management
    private final PortService portService;
    private final IDService idService;
    private final List<NetworkHandler> networkHandlers; //THe network handlers to easily interact with the network

    //Non final other values
    private DriverParent parent; //The driver parent for console and stuff
    private BridgeInstance bridgeInstance; //The bridge instance for service side management
    private INetworkConnection connection; //The current Executor for sending Packets and requests
    private IBukkit bukkit; //The provided bukkit features
    private PermissionPool permissionPool; //The current PermissionPool for perms management

    //Non-Final managers
    private ICloudPlayerManager playerManager; //Manages all players
    private IDatabaseManager databaseManager; //Manages database-entries
    private IServiceManager serviceManager; //Manages services
    private IGroupManager groupManager; //Manages groups
    private IConfigManager configManager; //Manages config
    private IScreenManager screenManager; //Manages screen

    //Final managers
    private final IReceiverManager receiverManager; //Manages receivers
    private final IServiceRegistry serviceRegistry; //Manages cloud-service-instances
    private final IEventManager eventManager; //Manages events
    private final IFallbackManager fallbackManager; //For fallback managing
    private final ITemplateManager templateManager; //Manages templates
    private final IModuleManager moduleManager; //Manage modules info
    private final IChannelMessenger messageManager; //Manage to message
    private final IMinecraftManager minecraftManager; //Manages minecraft
    private final ICommandManager commandManager; //Manages commands
    private final ICloudFlareManager cloudFlareManager; //Manages cloudflare
    private final ExecutorService executorService; //For task-execution
    private final RequestManager requestManager; //For request managing
    private final Scheduler scheduler;

    /**
     * Initialises the Driver with a Type
     *
     * @param driverType the type
     */
    public CloudDriver(CloudType driverType) {
        this(driverType, true);
    }

    /**
     * Initialises the Driver with a Type
     *
     * @param driverType the type
     */
    public CloudDriver(CloudType driverType, boolean installLibraries) {
        instance = this;
        this.driverType = driverType;

        //Setting default interface implementations
        this.scheduler = new SchedulerObject();
        this.serviceRegistry = new DefaultServiceRegistry();
        this.eventManager = new DefaultEventManager();
        this.receiverManager = new DefaultReceiverManager();
        this.fallbackManager = new DefaultFallbackManager();
        this.templateManager = new DefaultTemplateManager();
        this.moduleManager = new DefaultModuleManager();
        this.messageManager = new DefaultChannelMessenger();
        this.minecraftManager = new DefaultMinecraftManager();
        this.commandManager = new DefaultCommandManager();
        this.cloudFlareManager = new DefaultCloudFlareManager();

        //Setting other default values
        this.networkHandlers = new LinkedList<>();
        this.implementedData = new CloudMap<>();
        this.permissionPool = new PermissionPool();
        this.mojangPool = new UUIDPool(1);
        this.ticksPerSecond = new TicksPerSecond();
        this.idService = new IDService();
        this.portService = new PortService(25565, 30000);
        this.requestManager = new RequestManager();

        //Register Default-Services
        CloudDriver.getInstance().getServiceRegistry().registerService(new FileService());

        if (!CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE)) {
            this.serviceRegistry.getInstance(FileService.class).check();
        }

        //Check for libraries
        File libraryDirectory = driverType.equals(CloudType.BRIDGE) ? new File("../../../../../global/libs/") : this.serviceRegistry.getInstance(FileService.class).getLibraryDirectory();

        this.libraryService = new LibraryService(libraryDirectory, ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? ClassLoader.getSystemClassLoader() : null);

        if (installLibraries) {
            this.libraryService.installDefaultLibraries();
            if (this.driverType != CloudType.BRIDGE) {
                //Disable netty and mongoDB logging
                Loggers loggers = new Loggers((LoggerContext) LoggerFactory.getILoggerFactory(), new String[]{"io.netty", "org.mongodb.driver", "org.apache.http.impl.conn.PoolingHttpClientConnectionManager"});
                loggers.disable();
                AnsiConsole.systemInstall();
            }
        }


        //The executor service
        this.executorService = Executors.newCachedThreadPool(runnable -> {
            ThreadFactory threadFactory = Executors.defaultThreadFactory();
            Thread thread = threadFactory.newThread(runnable);
            thread.setName(String.format(Locale.ROOT, "PoolThread-%d", ThreadLocalRandom.current().nextInt(99999)));
            thread.setUncaughtExceptionHandler((thread1, e) -> { if (thread1 != null && !thread1.isInterrupted()) thread1.interrupt(); });
            thread.setDaemon(true);
            return thread;
        });
    }

    /*
     * ======================================
     * Messaging the CloudInstances
     * ======================================
     */

    /**
     * Logs a message to the console
     *
     * @param prefix the prefix between brackets
     * @param message the message behind brackets
     */
    public void log(String prefix, String message) {
        if (this.parent == null) {
            System.out.println("[" + prefix + "] " + ConsoleColor.stripColor(message));
            return;
        }
        this.parent.getConsole().sendMessage(prefix, message);
    }

    /**
     * Sends a message to the Cloud
     * 
     * @param prefix > Prefix of the action | Will look like this -> [PREFIX]
     * @param message > The message after the prefix
     * @param showUpInConsole > If false it will only be logged
     */
    public void messageCloud(String prefix, String message, boolean showUpInConsole) {
        if (driverType == CloudType.CLOUDSYSTEM) {
            this.log(prefix, message);
            return;
        }
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
    public void registerPacketHandler(IPacketHandler... handler) {
        for (IPacketHandler o : handler) {
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
    public void sendPacket(IPacket packet) {
        this.connection.sendPacket(packet);
    }

    /**
     * Sends a packet with a consumer
     * @param packet the packet to send
     * @param consumer the consumer to accept the response
     */
    public void sendPacket(IPacket packet, Consumer<Void> consumer) {
        this.sendPacket(packet);
        consumer.accept(null);
    }

    /*
     * ======================================
     *         Service Managing
     * ======================================
     */

    /**
     * Gets the Host for {@link IService}s to connect to
     *
     * @return inetAddress
     */
    public InetSocketAddress getAddress() {
        if (driverType == CloudType.BRIDGE) {
            JsonDocument jsonDocument = new JsonDocument(new File("./CLOUD/HYTORA-CLOUD.json"));
            return new InetSocketAddress(jsonDocument.getString("host"), jsonDocument.getInteger("port"));
        } else if (driverType == CloudType.RECEIVER) {
            IReceiver receiver = IReceiver.current();
            return new InetSocketAddress(receiver.getHost(), receiver.getPort());
        } else if (driverType == CloudType.CLOUDSYSTEM){
            NetworkConfig networkConfig  = this.configManager.getNetworkConfig();
            return new InetSocketAddress(networkConfig.getHost(), networkConfig.getPort());
        } else {
            return null;
        }
    }

    /*
     * ======================================
     *         Other Methods
     * ======================================
     */

    /**
     * Returns the Cloud prefix from the {@link MessageConfig}
     *
     * @return string prefix
     */
    public String getPrefix() {
        return this.configManager.getNetworkConfig().getMessageConfig().getPrefix().replace("&", "§");
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
     * Checks if an {@link AccessibleObject} should be executed async or sync
     * and if its delayed or not and which unit to use
     *
     * @param object the object where the annotation might be placed
     * @param runnable the runnable to execute
     */
    public void runTask(AccessibleObject object, Runnable runnable) {
        if (object.isAnnotationPresent(RunTaskSynchronous.class) || object.getClass().isAnnotationPresent(RunTaskSynchronous.class)) {
            RunTaskSynchronous runTaskSynchronous = object.getAnnotation(RunTaskSynchronous.class);
            if (runTaskSynchronous.delay() != -1 || runTaskSynchronous.unit() != TimeUnit.NANOSECONDS) {
                if (runTaskSynchronous.value()) {
                    CloudDriver.getInstance().getScheduler().scheduleDelayedTask(runnable, runTaskSynchronous.delay());
                } else {
                    CloudDriver.getInstance().getScheduler().scheduleDelayedTaskAsync(runnable, runTaskSynchronous.delay());
                }
            }
        } else {
            runnable.run();
        }
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
     * The {@link DriverInfo} of this driver
     *
     * @return cloud based information
     */
    public DriverInfo getInfo() {
        return CloudDriver.class.isAnnotationPresent(DriverInfo.class) ? CloudDriver.class.getAnnotation(DriverInfo.class) : null;
    }

    /*
     * ======================================
     *      Driver managing Methods
     * ======================================
     */

    /**
     * Reloads a certain {@link IService}
     */
    public void reload(IService service) {
        if (this.driverType == CloudType.BRIDGE) {
            this.sendPacket(new PacketReloadService(service));
        }
    }
    /**
     * Raw method to reload
     */
    public void reload() {
        for (ICloudService registeredService : CloudDriver.getInstance().getServiceRegistry().getRegisteredServices()) {
            registeredService.reload();
        }
        if (this.driverType == CloudType.BRIDGE) {
            this.sendPacket(new PacketReload());
        } else if (driverType == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().getEventManager().callEvent(new DriverEventReload());
        }
    }

    /**
     * Raw method to shutdown
     */
    public void shutdown() {
        for (ICloudService registeredService : CloudDriver.getInstance().getServiceRegistry().getRegisteredServices()) {
            registeredService.save();
        }
        this.mojangPool.shutdown();
    }

}
