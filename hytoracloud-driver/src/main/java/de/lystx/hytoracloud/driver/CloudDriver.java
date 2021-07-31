package de.lystx.hytoracloud.driver;

import ch.qos.logback.classic.LoggerContext;
import de.lystx.hytoracloud.driver.bridge.BridgeInstance;
import de.lystx.hytoracloud.driver.bridge.ProxyBridge;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.color.ConsoleColor;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.DefaultModuleManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.IModuleManager;
import de.lystx.hytoracloud.driver.cloudservices.global.messenger.DefaultChannelMessenger;
import de.lystx.hytoracloud.driver.cloudservices.global.messenger.IChannelMessenger;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.fallback.DefaultFallbackManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.fallback.IFallbackManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.template.DefaultTemplateManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.template.ITemplateManager;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.interfaces.*;
import de.lystx.hytoracloud.driver.commons.minecraft.DefaultMinecraftManager;
import de.lystx.hytoracloud.driver.commons.minecraft.IMinecraftManager;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketReload;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketReloadService;
import de.lystx.hytoracloud.driver.commons.receiver.DefaultReceiverManager;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiverManager;
import de.lystx.hytoracloud.driver.commons.requests.RequestManager;
import de.lystx.hytoracloud.driver.commons.service.IDService;
import de.lystx.hytoracloud.driver.commons.service.PortService;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketCallEvent;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketLogMessage;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommand;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.service.IEventManager;
import de.lystx.hytoracloud.driver.cloudservices.global.main.DefaultServiceRegistry;
import de.lystx.hytoracloud.driver.cloudservices.global.main.IServiceRegistry;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabaseManager;
import de.lystx.hytoracloud.driver.bridge.IBukkit;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ObjectCloudPlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.ObjectServiceManager;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.cloudservices.cloud.log.Loggers;
import de.lystx.hytoracloud.driver.commons.minecraft.other.TicksPerSecond;
import de.lystx.hytoracloud.driver.utils.CloudRunnable;
import de.lystx.hytoracloud.driver.commons.storage.CloudMap;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.MessageConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.service.DefaultEventManager;
import de.lystx.hytoracloud.driver.cloudservices.global.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.lib.LibraryService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.uuid.UUIDPool;
import lombok.Getter;
import lombok.Setter;
import de.lystx.hytoracloud.networking.connection.NetworkConnection;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
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
        todo = {
                "1.17 Support & Higher Java Versions",
                "Check Velocity-Support",
                "Fix TabList updating",
                "[Information] TabCompletion is beta!"
        }
)
public class CloudDriver {

    @Getter
    private static CloudDriver instance; //The global driver instance

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
    private ProxyBridge proxyBridge; //The proxy bridge for player and proxy management
    private BridgeInstance bridgeInstance; //The bridge instance for service side management
    private NetworkConnection connection; //The current Executor for sending Packets and requests
    private IBukkit bukkit; //The provided bukkit features

    @Setter
    private PermissionPool permissionPool; //The current PermissionPool for perms management
    private NetworkConfig networkConfig; //The network config of this instance

    //Non-Final managers
    private ObjectCloudPlayerManager playerManager; //Manages all players
    private IDatabaseManager databaseManager; //Manages database-entries
    private ObjectServiceManager serviceManager; //Manages services

    //Final managers
    private final IReceiverManager receiverManager; //Manages receivers
    private final IServiceRegistry serviceRegistry; //Manages cloud-service-instances
    private final IEventManager eventManager; //Manages events
    private final IFallbackManager fallbackManager; //For fallback managing
    private final ITemplateManager templateManager; //Manages templates
    private final IModuleManager moduleManager; //Manage modules info
    private final IChannelMessenger messageManager; //Manage to message
    private final IMinecraftManager minecraftManager; //Manages minecraft
    private final ExecutorService executorService; //For task-execution
    private final RequestManager requestManager; //For request managing


    /**
     * Initialises the Driver with a Type
     *
     * @param driverType the type
     */
    public CloudDriver(CloudType driverType) {
        instance = this;
        this.driverType = driverType;

        //Setting default interface implementations
        this.serviceRegistry = new DefaultServiceRegistry();
        this.eventManager = new DefaultEventManager();
        this.receiverManager = new DefaultReceiverManager();
        this.fallbackManager = new DefaultFallbackManager();
        this.templateManager = new DefaultTemplateManager();
        this.moduleManager = new DefaultModuleManager();
        this.messageManager = new DefaultChannelMessenger();
        this.minecraftManager = new DefaultMinecraftManager();

        //Setting other default values
        this.networkHandlers = new LinkedList<>();
        this.implementedData = new CloudMap<>();
        this.permissionPool = new PermissionPool();
        this.mojangPool = new UUIDPool(1);
        this.networkConfig = NetworkConfig.defaultConfig();
        this.ticksPerSecond = new TicksPerSecond();
        this.idService = new IDService();
        this.portService = new PortService(25565, 30000);
        this.requestManager = new RequestManager();

        //Register Default-Services
        CloudDriver.getInstance().getServiceRegistry().registerService(new FileService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new Scheduler());
        CloudDriver.getInstance().getServiceRegistry().registerService(new CommandService());

        //Check for libraries
        File libraryDirectory = driverType.equals(CloudType.BRIDGE) ? new File("../../../../../global/libs/") : this.getInstance(FileService.class).getLibraryDirectory();

        this.libraryService = new LibraryService(libraryDirectory, ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? ClassLoader.getSystemClassLoader() : null);
        this.libraryService.installDefaultLibraries();

        if (this.driverType != CloudType.BRIDGE) {
            //Disable netty and mongoDB logging
            Loggers loggers = new Loggers((LoggerContext) LoggerFactory.getILoggerFactory(), new String[]{"io.netty", "org.mongodb.driver", "org.apache.http.impl.conn.PoolingHttpClientConnectionManager"});
            loggers.disable();
            AnsiConsole.systemInstall();
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
     *    Command and EventManaging
     * ======================================
     */

    /**
     * This method registers a CloudCommand class object
     * And all Methods in this class that have the {@link CommandExecutor}
     * and the {@link String[]} parameter will be cached to be executed after
     * And the method should have a {@link de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command}-Annotation
     * to declare its really a command that executed
     *
     * @param command the class object
     */
    public void registerCommand(Object command) {
        this.getInstance(CommandService.class).registerCommand(command);
    }

    /**
     * Unregisters a command if the command object
     * has been registered before using {@link CloudDriver#registerCommand(Object)}
     *
     * @param command the command
     */
    public void unregisterCommand(Object command) {
        this.getInstance(CommandService.class).unregisterCommand(command);
    }

    /**
     * Calls an Event with the driver 
     * If this instance is bridge it calls an event
     * and sets this service on blacklist to receive the same event again
     * to prevent double-executing events
     * 
     * If this instance is cloud it just sends packets to all
     * clients and sets the cloud on blacklist to receive the same event again
     *
     * @param cloudEvent the event to call
     */
    public boolean callEvent(CloudEvent cloudEvent) {
        if ((this.serviceManager == null || this.serviceManager.getCurrentService() == null)&& this.driverType == CloudType.BRIDGE) {
            return false;
        }

        if (this.driverType == CloudType.BRIDGE) {
            if (this.connection != null) {
                this.connection.sendPacket(new PacketCallEvent(cloudEvent, this.serviceManager.getCurrentService().getName()));
            }
        } else {
            if (this.connection != null) {
                this.connection.sendPacket(new PacketCallEvent(cloudEvent, "cloud"));
            }
        }
        return this.eventManager.callEvent(cloudEvent);
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
        this.parent.getConsole().getLogger().sendMessage(prefix, message);
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
    public void sendPacket(Packet packet) {
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
    public void sendPacket(Packet packet, Consumer<Component> consumer) {
        if (consumer == null) {
            this.connection.sendPacket(packet);
        } else {
            Component reply = packet.toReply(connection);
            consumer.accept(reply);
        }
    }

    /**
     * Transfers a {@link Packet} to a {@link Component}
     * with default timeOut of 3000ms
     *
     * @param packet the packet
     * @return response
     */
    public Component getResponse(Packet packet) {
        return this.getResponse(packet, 3000);
    }

    /**
     * Transfers a {@link Packet} to a {@link Component}
     *
     * @param responsePacket the packet
     * @param timeOut the timeout
     * @return response
     */
    public Component getResponse(Packet responsePacket, int timeOut) {
        return responsePacket.toReply(connection, timeOut);
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
    public InetSocketAddress getCloudAddress() {
        if (driverType == CloudType.BRIDGE) {
            JsonDocument jsonDocument = new JsonDocument(new File("./CLOUD/HYTORA-CLOUD.json"));
            return new InetSocketAddress(jsonDocument.getString("host"), jsonDocument.getInteger("port"));
        } else if (driverType == CloudType.RECEIVER) {
            IReceiver receiver = IReceiver.current();
            return new InetSocketAddress(receiver.getHost(), receiver.getPort());
        } else if (driverType == CloudType.CLOUDSYSTEM){
            NetworkConfig networkConfig = getInstance(ConfigService.class).getNetworkConfig();
            return new InetSocketAddress(networkConfig.getHost(), networkConfig.getPort());
        } else {
            return null;
        }
    }

    /**
     * Loads the {@link ProxyConfig} if this is the CloudBridge
     *
     * @return proxy config or null
     */
    public ProxyConfig getProxyConfig() {
        if (this.driverType != CloudType.BRIDGE) {
            return null;
        }
        NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();
        if (networkConfig == null || CloudDriver.getInstance().getServiceManager().getCurrentService() == null) {
            return ProxyConfig.defaultConfig();
        }
        ProxyConfig proxyConfig = networkConfig.getProxyConfigs().get(CloudDriver.getInstance().getServiceManager().getCurrentService().getGroup().getName());
        return proxyConfig == null ? ProxyConfig.defaultConfig() : proxyConfig;
    }

    /**
     * The {@link ServiceType} of the current process
     * Comparable with {@link CloudDriver#getDriverType()}
     * to identify the process and determine if allowed
     *
     * @return type
     */
    public ServiceType getServiceType() {
        if (this.driverType == CloudType.CLOUDSYSTEM) {
            return ServiceType.CLOUDSYSTEM;
        } else {
            if (this.serviceManager == null || this.serviceManager.getCurrentService() == null) {
                return ServiceType.NONE;
            } else {
                return this.serviceManager.getCurrentService().getGroup().getType();
            }
        }
    }

    /*
     * ======================================
     *         Other Methods
     * ======================================
     */


    /**
     * Sets the network config and updates the port values
     *
     * @param networkConfig config
     */
    public void setNetworkConfig(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
        try {
            this.portService.setServerPort(networkConfig.getServerStartPort());
            this.portService.setProxyPort(networkConfig.getProxyStartPort());
        } catch (NullPointerException e) {
            //Ignoring
        }
    }

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

    /**
     * Returns the Cloud prefix from the {@link MessageConfig}
     *
     * @return string prefix
     */
    public String getPrefix() {
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
                    Scheduler.getInstance().scheduleDelayedTask(runnable, runTaskSynchronous.delay());
                } else {
                    Scheduler.getInstance().scheduleDelayedTaskAsync(runnable, runTaskSynchronous.delay());
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
     * Gets the version via {@link DriverInfo} annotation
     *
     * @return current version of cloud
     */
    public String getVersion() {
        return driverInfo() == null ? "UNKNOWN" : driverInfo().version();
    }

    /**
     * The {@link DriverInfo} of this driver
     *
     * @return cloud based information
     */
    public DriverInfo driverInfo() {
        if (CloudDriver.class.isAnnotationPresent(DriverInfo.class)) {
            return CloudDriver.class.getAnnotation(DriverInfo.class);
        } else {
            return null;
        }
    }

    /*
     * ======================================
     *         Driver managing Methods
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
