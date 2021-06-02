package de.lystx.hytoracloud.driver;

import ch.qos.logback.classic.LoggerContext;
import de.lystx.hytoracloud.driver.elements.interfaces.BooleanRequest;
import de.lystx.hytoracloud.driver.elements.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.other.ReceiverInfo;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketCallEvent;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInCopyTemplate;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInCreateTemplate;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInLogMessage;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketCommand;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestModules;
import de.lystx.hytoracloud.driver.elements.packets.result.ResultPacketStatistics;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.service.event.IEventService;
import de.lystx.hytoracloud.driver.service.main.DefaultServiceRegistry;
import de.lystx.hytoracloud.driver.service.main.IServiceRegistry;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.service.config.impl.fallback.Fallback;
import de.lystx.hytoracloud.driver.service.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.service.config.stats.Statistics;
import de.lystx.hytoracloud.driver.service.config.stats.StatsService;
import de.lystx.hytoracloud.driver.service.database.IDatabaseManager;
import de.lystx.hytoracloud.driver.service.messenger.IChannelMessenger;
import de.lystx.hytoracloud.driver.service.module.Module;
import de.lystx.hytoracloud.driver.service.module.ModuleInfo;
import de.lystx.hytoracloud.driver.service.module.ModuleService;
import de.lystx.hytoracloud.driver.service.other.IBukkit;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.service.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.server.IServiceManager;
import de.lystx.hytoracloud.driver.service.server.impl.TemplateService;
import de.lystx.hytoracloud.driver.service.server.other.process.Threader;
import de.lystx.hytoracloud.driver.service.util.Utils;
import de.lystx.hytoracloud.driver.service.util.log.Loggers;
import de.lystx.hytoracloud.driver.service.util.minecraft.TicksPerSecond;
import de.lystx.hytoracloud.driver.service.util.reflection.Reflections;
import de.lystx.hytoracloud.driver.service.util.utillity.CloudRunnable;
import de.lystx.hytoracloud.driver.service.util.utillity.CloudMap;
import de.lystx.hytoracloud.driver.service.util.utillity.Value;
import de.lystx.hytoracloud.driver.service.config.impl.MessageConfig;
import de.lystx.hytoracloud.driver.service.event.DefaultEventService;
import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import io.thunder.Thunder;
import io.thunder.connection.ErrorHandler;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.lib.LibraryService;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.impl.response.IResponse;
import io.thunder.packet.impl.response.PacketRespond;
import io.thunder.packet.impl.response.Response;
import io.thunder.packet.impl.response.ResponseStatus;
import io.thunder.utils.logger.LogLevel;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;


public class CloudDriver {


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
    private ThunderConnection connection;

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
    private ICloudPlayerManager cloudPlayerManager;

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
     * To manage all channelMessages
     */
    @Getter
    private IChannelMessenger channelMessenger;

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
     * Inits the Driver with a Type
     * @param driverType the type
     */
    public CloudDriver(CloudType driverType) {
        instance = this;

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
                onError(e);
            }
        });

        this.serviceRegistry = new DefaultServiceRegistry();
        this.eventService = new DefaultEventService();
        this.driverType = driverType;


        this.cloudInventories = new HashMap<>();
        this.networkHandlers = new LinkedList<>();
        this.implementedData = new CloudMap<>();

        this.permissionPool = new PermissionPool();
        this.implementedData.put("networkConfig", NetworkConfig.defaultConfig());

        //Check for libraries and colored console
        if (driverType.equals(CloudType.RECEIVER) || driverType.equals(CloudType.CLOUDSYSTEM) || driverType.equals(CloudType.NONE)) {
            this.libraryService = new LibraryService(new File("./local/libs/"), ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? ClassLoader.getSystemClassLoader() : null);
            this.libraryService.installDefaultLibraries();
            AnsiConsole.systemInstall();

            //Disable netty and mongoDB logging
            Loggers loggers = new Loggers((LoggerContext) LoggerFactory.getILoggerFactory(), new String[]{"io.netty", "org.mongodb.driver"});
            loggers.disable();
        } else {
            this.libraryService = new LibraryService(new File("../../../../../libs/"), ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? ClassLoader.getSystemClassLoader() : null);
            this.libraryService.installDefaultLibraries();
        }

        //Register Default-Services
        CloudDriver.getInstance().getServiceRegistry().registerService(new FileService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new Scheduler());
        CloudDriver.getInstance().getServiceRegistry().registerService(new DefaultEventService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new CommandService());

        //Register extra features
        this.ticksPerSecond = new TicksPerSecond(this);

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
     * @param commandObject the command
     */
    public void registerCommand(Object commandObject) {
        this.getInstance(CommandService.class).unregisterCommand(commandObject);
    }

    /**
     * Unregisters a Command
     * @param commandObject the command
     */
    public void unregisterCommand(Object commandObject) {
        this.getInstance(CommandService.class).unregisterCommand(commandObject);
    }

    /**
     * Calls an Event with the
     * @param cloudEvent the event to call
     */
    public boolean callEvent(CloudEvent cloudEvent) {
        if (this.connection != null) {
            this.connection.sendPacket(new PacketCallEvent(cloudEvent));
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
        this.sendPacket(new PacketInLogMessage(prefix, message, showUpInConsole));
    }

    /**
     * Sends a message to the cloudSystem
     * @param prefix > Prefix of action
     * @param message > The message after prefix
     */
    public void messageCloud(String prefix, Object message) {
        this.messageCloud(prefix, String.valueOf(message), true);
    }


    /**
     * Sends a packet and waits till another is send back
     * and it it matches the expected class the consumer will accept
     *
     * @param packet the packet to send
     * @param packetToExpect the class to expect
     */
    public <T extends Packet> void waitForPacket(Packet packet, Class<T> packetToExpect, Consumer<T> consumer) {
        this.sendPacket(packet);
        this.registerPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                if (packet.getClass().equals(packetToExpect)) {
                    consumer.accept((T) packet);
                }
            }
        });
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
            connection.addPacketHandler(o);
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
     * Sends a packet with a consumer
     * @param packet the packet to send
     * @param consumer the consumer to accept the response
     */
    public void sendPacket(Packet packet, Consumer<Response> consumer) {
        if (consumer == null) {
            this.connection.sendPacket(packet);
        } else {
            this.connection.sendPacket(packet, consumer);
        }
    }

    /**
     * Sends a packet with a consumer
     * @param packet the packet to send
     * @param timeOut the timeOut for the response
     * @param consumer the consumer to accept the response
     */
    public void sendPacket(Packet packet, Consumer<Response> consumer, int timeOut) {
        this.connection.sendPacket(packet, consumer, timeOut);
    }

    /**
     * Transfers a {@link Packet} to a {@link Response}
     * with default timeOut of 3000ms
     *
     * @param packet the packet
     * @return response
     */
    public Response getResponse(Packet packet) {
        return this.getResponse(packet, 3000);
    }

    /**
     * Transfers a {@link Packet} to a {@link Response}
     *
     * @param responsePacket the packet
     * @param timeOut the timeout
     * @return response
     */
    public Response getResponse(Packet responsePacket, int timeOut) {
        return this.connection.transferToResponse(responsePacket, timeOut);
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
     * Returns the current {@link Service} the Driver is running on
     * might be Lobby or Proxy whatever
     *
     * @return service
     */
    public Service getThisService() {
        return new JsonBuilder(new File("./CLOUD/connection.json")).getAs(Service.class);
    }

    /**
     * Gets the Host for {@link Service}s to connect to
     *
     * @return inetAddress
     */
    public InetSocketAddress getHost() {
        if (driverType == CloudType.CLOUDAPI) {
            JsonBuilder jsonBuilder = new JsonBuilder(new File("./CLOUD/cloud.json"));
            return new InetSocketAddress(jsonBuilder.getString("host"), jsonBuilder.getInteger("port"));
        } else if (driverType == CloudType.CLOUDSYSTEM) {
            NetworkConfig networkConfig = getInstance(ConfigService.class).getNetworkConfig();
            return new InetSocketAddress(networkConfig.getHost(), networkConfig.getPort());
        } else {
            ReceiverInfo receiverInfo = this.implementedData.getObject("receiverInfo", ReceiverInfo.class);
            return new InetSocketAddress(receiverInfo.getIpAddress(), receiverInfo.getPort());
        }
    }

    /**
     * Returns the {@link ProxyConfig}
     * @return the proxyConfig
     */
    public ProxyConfig getProxyConfig() {
        if (driverType != CloudType.CLOUDAPI) {
            throw new UnsupportedOperationException("Not available for " + driverType + "!");
        }

        return CloudDriver.getInstance().getThisService().getServiceGroup().getProperties().get("proxyConfig", ProxyConfig.class);
    }

    /*
     * ======================================
     *         Other Methods
     * ======================================
     */

    /**
     * Returns the {@link NetworkConfig}
     *
     * @return config
     */
    public NetworkConfig getNetworkConfig() {
        if (driverType == CloudType.CLOUDAPI || driverType == CloudType.RECEIVER) {
            return implementedData.getObject("networkConfig", NetworkConfig.class);
        } else {
            return getInstance(ConfigService.class).getNetworkConfig();
        }
    }

    /**
     * Returns the Cloud prefix from the {@link MessageConfig}
     *
     * @return string prefix
     */
    public String getCloudPrefix() {
        if (getNetworkConfig() == null) {
            return "§8[§cNullCloud§8]";
        }
        return getNetworkConfig().getMessageConfig().getPrefix().replace("&", "§");
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
     * @param player
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
     * Returns statistics of Cloud by Query
     * @return statistics
     */
    @SneakyThrows
    public Statistics getStatistics() {
        if (this.driverType == CloudType.CLOUDAPI) {
            return new Statistics(new VsonObject(connection.transferToResponse(new ResultPacketStatistics()).getMessage()));
        } else {
            return getInstance(StatsService.class).getStatistics();
        }
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
     * @return IResponse
     */
    public IResponse<List<ModuleInfo>> getModules() {

        if (driverType == CloudType.CLOUDAPI) {
            Response response = connection.transferToResponse(new PacketRequestModules(), 5000);
            return response.toIResponse(response.get(0).asList(ModuleInfo.class));
        } else {


            return new IResponse<List<ModuleInfo>>() {
                @Override
                public List<ModuleInfo> get() {
                    List<ModuleInfo> list = new LinkedList<>();
                    if (getInstance(ModuleService.class) != null) {
                        for (Module module : getInstance(ModuleService.class).getModules()) {
                            list.add(module.getInfo());
                        }
                    }
                    return list;
                }

                @Override
                public ResponseStatus getStatus() {
                    return ResponseStatus.SUCCESS;
                }

                @Override
                public UUID getUniqueId() {
                    return UUID.randomUUID();
                }

                @Override
                public String getMessage() {
                    return "No Message";
                }

                @Override
                public Response raw() {
                    return null;
                }
            };
        }
    }


    /**
     * Returns a module by name
     * @param name of the Module
     * @return info of module
     */
    @SneakyThrows
    public ModuleInfo getModule(String name) {
        if (this.driverType == CloudType.CLOUDAPI) {
            IResponse<List<ModuleInfo>> iResponse = this.getModules();

            return iResponse.raw().get(0).asList(ModuleInfo.class)
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
     * This will lead to {@link Threader#execute(Runnable)}
     * to execute something in a thread created with
     * a {@link java.util.concurrent.ThreadFactory}
     * @param runnable the runnable to run
     */
    public void execute(Runnable runnable) {
        Threader.getInstance().execute(runnable);
    }


    /*
     * ======================================
     *      Template Managing
     * ======================================
     */

    /**
     * Copies a server into a specific Template
     * @param service
     * @param template
     */
    public void copyTemplate(Service service, String template) {
        this.copyTemplate(service, template, null);
    }

    /**
     * Copies a server into a specific Template
     * but it only copies a specific folder like "world"
     * or the "plugins" folder or "plugins/YourFolder"
     * @param service
     * @param template
     */
    public void copyTemplate(Service service, String template, String specificDirectory) {
        if (driverType == CloudType.CLOUDAPI) {
            PacketInCopyTemplate packetInCopyTemplate = new PacketInCopyTemplate(service, template, specificDirectory);
            this.sendPacket(packetInCopyTemplate);
            return;
        }
        TemplateService instance = getInstance(TemplateService.class);

        instance.copy(service, template, specificDirectory);
    }

    /**
     * Creates a Template for a group
     * @param group
     * @param template
     */
    public void createTemplate(ServiceGroup group, String template) {
        if (driverType == CloudType.CLOUDAPI) {
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
     * @param player
     * @return
     */
    public boolean isFallback(CloudPlayer player) {
        Value<Boolean> booleanValue = new Value<>(false);
        this.getFallbacks(player).forEach(fallback -> {
            if (player.getService().getServiceGroup().getName().equalsIgnoreCase(fallback.getGroupName())) {
                booleanValue.setValue(true);
            }
        });
        return booleanValue.getValue();
    }


    /**
     * Returns {@link Service} of
     * Fallback for {@link CloudPlayer}
     * @param player
     * @return
     */
    public Service getFallback(CloudPlayer player) {
        try {
            Fallback fallback = this.getHighestFallback(player);
            Service service;
            try {
                service = CloudDriver.getInstance().getServiceManager().getServices(CloudDriver.getInstance().getServiceManager().getServiceGroup(fallback.getGroupName())).get(new Random().nextInt(CloudDriver.getInstance().getServiceManager().getServices(CloudDriver.getInstance().getServiceManager().getServiceGroup(fallback.getGroupName())).size()));
            } catch (Exception e){
                service = CloudDriver.getInstance().getServiceManager().getService(fallback.getGroupName() + "-1");
            }
            return service;
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Gets Fallback with highest
     * ID (Example sorting 1, 2, 3)
     * @param player
     * @return
     */
    public Fallback getHighestFallback(CloudPlayer player) {
        List<Fallback> list = this.getFallbacks(player);
        list.sort(Comparator.comparingInt(Fallback::getPriority));
        return list.get(list.size() - 1) == null ? CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback() : list.get(list.size() - 1);
    }

    /**
     * Iterates through all Fallbacks
     * if permission of fallback is null
     * or player has fallback permission
     * adds it to a list
     * @param player
     * @return
     */
    public List<Fallback> getFallbacks(CloudPlayer player) {
        List<Fallback> list = new LinkedList<>();
        list.add(CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback());
        CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getFallbacks().forEach(fallback -> {
            if (CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), fallback.getPermission()) || fallback.getPermission().trim().isEmpty() || fallback.getPermission() == null) {
                list.add(fallback);
            }
        });
        return list;
    }

    /*
     * ======================================
     *         Raw extentions Methods
     * ======================================
     */

    public void shutdownDriver() {
        CloudDriver.getInstance().sendPacket(new PacketInStopServer(CloudDriver.getInstance().getThisService()));
        this.connection.disconnect();
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
