package de.lystx.cloudapi;


import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.standalone.handler.*;
import de.lystx.cloudapi.standalone.manager.CloudNetwork;
import de.lystx.cloudapi.standalone.manager.CloudPlayers;
import de.lystx.cloudapi.standalone.manager.Fallbacks;
import de.lystx.cloudapi.standalone.manager.Templates;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.interfaces.CloudService;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.result.other.ResultPacketModules;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.CustomPacket;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketInLogMessage;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInRegister;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInStopServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketCommand;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.packets.result.Result;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.elements.packets.result.other.ResultPacketStatistics;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.config.stats.Statistics;
import de.lystx.cloudsystem.library.service.event.Event;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.lib.Repository;
import de.lystx.cloudsystem.library.service.module.ModuleInfo;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketState;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.featured.labymod.LabyModAddon;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.server.other.process.Threader;
import de.lystx.cloudsystem.library.elements.interfaces.Acceptable;
import de.lystx.cloudsystem.library.Cloud;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.util.Value;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Getter @Setter
public class CloudAPI implements CloudService {

    private static CloudAPI instance;

    private NetworkConfig networkConfig;
    private PermissionPool permissionPool;

    private final CloudLibrary cloudLibrary;
    private final CloudClient cloudClient;
    private final CloudNetwork network;
    private final Fallbacks fallbacks;
    private final Templates templates;
    private final CloudPlayers cloudPlayers;
    private final ExecutorService executorService;
    private final CommandService commandService;
    private final EventService eventService;

    private boolean nametags;
    private boolean useChat;
    private boolean joinable;
    private boolean newVersion;
    private String chatFormat;

    public CloudAPI() {
        instance = this;

        this.cloudLibrary = new CloudLibrary(CloudType.CLOUDAPI);
        this.cloudClient =  this.cloudLibrary.getCloudClient();
        this.executorService = Executors.newCachedThreadPool();

        this.network = new CloudNetwork(this);
        this.fallbacks = new Fallbacks(this);
        this.cloudPlayers = new CloudPlayers(this);
        this.permissionPool = new PermissionPool(cloudLibrary);
        this.templates = new Templates(this);
        this.commandService = new CommandService(this.cloudLibrary, "Command", de.lystx.cloudsystem.library.service.CloudService.CloudServiceType.MANAGING);
        this.eventService = this.cloudLibrary.getService(EventService.class);

        Cloud.getInstance().setCurrentCloudExecutor(this.cloudClient);
        Cloud.getInstance().setPermissionPool(this.permissionPool);
        Cloud.getInstance().setCurrentCloudType(CloudType.CLOUDAPI);
        this.execute(LabyModAddon::load);

        this.chatFormat = "%prefix%%player% §8» §7%message%";
        this.useChat = false;
        this.nametags = false;
        this.joinable = false;

        this.registerPacketHandler(
                new PacketHandlerConfig(this),
                new PacketHandlerCommand(this),
                new PacketHandlerNetwork(this),
                new PacketHandlerSubChannel(this),
                new PacketHandlerCommunication(this),
                new PacketHandlerPlayer(this),
                new PacketHandlerPermissionPool(this),
                new PacketHandlerCallEvent(this)
        ).bootstrap();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "shutdown_hook"));
    }

    /**
     * Registers a PacketHandler
     * @param handler
     */
    public CloudAPI registerPacketHandler(Object... handler) {
        this.cloudClient.registerPacketHandlers(handler);
        return this;
    }

    /**
     * Registers {@link NetworkHandler}s
     * @param networkHandlers
     */
    public CloudAPI registerNetworkHandler(NetworkHandler... networkHandlers) {
        for (NetworkHandler networkHandler : networkHandlers) {
            this.cloudClient.registerHandler(networkHandler);
        }
        return this;
    }
    /**
     * Registers a Command
     * @param commandObject
     */
    public CloudAPI registerCommand(Object commandObject) {
        this.commandService.registerCommand(commandObject);
        return this;
    }

    /**
     * Unregisters a Command
     * @param commandObject
     */
    public void unregisterCommand(Object commandObject) {
        this.commandService.unregisterCommand(commandObject);
    }

    /**
     * Installs a Maven Library and loads it internally
     * from a REPO of your choice
     * @param groupId > The GroupID
     * @param artifactId > The ArtifactID
     * @param version > The Version
     * @param repo > The Repository
     */
    public void installMaven(String groupId, String artifactId, String version, Repository repo) {
        this.cloudLibrary.getLibraryService().install(groupId, artifactId, version, repo);
    }

    /**
     * This will execute a Query
     * but asynchronous
     * @param packet
     * @param consumer
     */
    public <T> void sendQuery(ResultPacket<T> packet, Consumer<Result<T>> consumer) {
        this.executorService.execute(() -> this.sendQuery(packet).onResultSet(consumer));
    }

    /**
     * Returns all the Modules
     * @return
     */
    public List<ModuleInfo> getModules() {
        return this.sendQuery(new ResultPacketModules()).getResult();
    }

    /**
     * Returnms a module by name
     * @param name of the Module
     * @return
     */
    public ModuleInfo getModule(String name) {
        List<ModuleInfo> moduleInfos = this.getModules();

        return moduleInfos
                .stream()
                .filter(
                        moduleInfo ->
                                moduleInfo.getName().equalsIgnoreCase(name)
                )
                .findFirst()
                .orElse(null);
    }

    public <T> Result<T> sendQuery(ResultPacket<T> packet) {
        return this.sendQuery(packet, 3000);
    }

    /**
     * Sends a Query to the Cloud
     * This will send a {@link ResultPacket} to the Cloud
     * The Cloud will call the method {@link ResultPacket#read(CloudLibrary)}
     * This method returns a VsonObject which will be put in a {@link Result}
     * and will be send back to all services.
     * If the UUID of the Result matches with the created UUID at the top
     * The Main-Thread will continue and the Result can be returned!
     *
     * @param packet > The ResultPacket to send
     * @return Result from CloudLibrary
     */
    public <T> Result<T> sendQuery(ResultPacket<T> packet, int timeout) {
        Value<Result<T>> value = new Value<>();
        UUID uuid = UUID.randomUUID();

        this.sendPacket(packet.uuid(uuid), packetState -> {
            if (packetState == PacketState.FAILED) {
                Result<T> r = new Result<>(uuid, null);
                r.setThrowable(new IllegalAccessError("Could not create Query for " + packet.getClass().getSimpleName() + " because PacketState returned " + PacketState.FAILED.name() + "!"));
                value.setValue(r);
                Thread.currentThread().interrupt();
            }
        });
        this.cloudClient.registerPacketHandler(new PacketHandlerAdapter() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof ResultPacket) {
                    ResultPacket<T> resultPacket = (ResultPacket<T>)packet;
                    if (uuid.equals(resultPacket.getUniqueId())) {
                        value.setValue(resultPacket.getResult());
                        cloudClient.getPacketAdapter().unregisterAdapter(this);
                    }
                }
            }
        });
        int count = 0;

        while (value.getValue() == null && count++ < timeout) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        if (count >= timeout) {
            Result<T> r = new Result<>(uuid, null);
            r.setThrowable(new TimeoutException("Request timed out!"));
            value.setValue(r);
        }
        return value.getValue();
    }

    /**
     * This will lead to {@link Threader#execute(Runnable)}
     * to execute something in a thread created with
     * a {@link java.util.concurrent.ThreadFactory}
     * @param runnable
     */
    public void execute(Runnable runnable) {
        Threader.getInstance().execute(runnable);
    }

    /**
     * This will boot up the {@link CloudAPI}
     * It will start a new {@link Thread}
     * which starts the CloudClient
     * If no connection could be built up
     * the serviec will stop
     */
    public void bootstrap() {
        try {
            this.cloudClient.onConnectionEstablish(nettyClient -> nettyClient.sendPacket(new PacketInRegister(this.getService())));
            this.cloudClient.connect(this.getService().getHost(), this.getService().getCloudPort());
        } catch (Exception e) {
            System.out.println("[CloudAPI] Couldn't connect to CloudSystem! Stopping...");
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void shutdown() {
        this.shutdown(null);
    }

    public void shutdown(Consumer<PacketState> consumer) {
        this.sendPacket(new PacketInStopServer(this.getService()), packetState -> {
            this.cloudClient.disconnect();
            consumer.accept(packetState);
        });
    }

    /**
     * Makes the Cloud execute a command
     * @param command
     */
    public void sendCommand(String command) {
        this.sendPacket(new PacketCommand("null", command));
    }

    /**
     * Sends a packet to the the cloudSystem
     * Without consumer to call back
     * @param packet
     */
    public void sendPacket(Packet packet) {
        this.sendPacket(packet, (Consumer<PacketState>) null);
    }

    /**
     * Sends a packet with a consumer
     * @param packet
     * @param consumer
     */
    public void sendPacket(Packet packet, Consumer<PacketState> consumer) {
        if (packet.getClass().getName().toLowerCase().contains("de.lystx.cloudsystem.library.elements.packets")) {
            this.cloudClient.sendPacket(packet, consumer);
        } else {
            this.cloudClient.sendPacket(new CustomPacket(packet), consumer);
        }
    }

    /**
     * Sends a packet and auto
     * registers a PacketHandler which unregisters if the
     * {@link Acceptable} returns true
     * @param packet
     * @param packetHandler
     */
    public void sendPacket(Packet packet, Acceptable<Packet> packetHandler) {
        if (packetHandler != null) {
            this.cloudClient.registerPacketHandler(new PacketHandlerAdapter() {
                @Override
                public void handle(Packet packet) {
                    if (packetHandler.isAccepted(packet)) {
                        cloudClient.getPacketAdapter().unregisterAdapter(this);
                    }
                }
            });
        }
        this.sendPacket(packet);
    }

    /**
     * Calls an Event within the CloudLibrary
     * @param event
     */
    public void callEvent(Event event) {
        this.cloudLibrary.callEvent(event);
    }

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
     * @param prefix
     * @param message
     */
    public void messageCloud(String prefix, Object message) {
        this.messageCloud(prefix, String.valueOf(message), true);
    }

    /**
     * Returns current Service
     * Loaded by Document getService()
     * @return
     */
    public Service getService() {
       return this.getDocument().getAs(Service.class);
    }

    /**
     * Returns the {@link ProxyConfig}
     * @return
     */
    public ProxyConfig getProxyConfig() {
        return CloudAPI.getInstance().getService().getServiceGroup().getValues().get("proxyConfig", ProxyConfig.class);
    }

    /**
     * Loads Properties of this service
     * @return SerializableDocument
     */
    public SerializableDocument getProperties() {
        return this.getService().getProperties();
    }

    /**
     * Loads VsonObject of this Service
     * By file > given by ServerStartup
     * @return
     */
    public VsonObject getDocument() {
        try {
            return new VsonObject(new File("./CLOUD/connection.json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Returns Prefix of CloudSystem
     * Getting from {{@link de.lystx.cloudsystem.library.service.config.impl.MessageConfig}}
     * @return
     */
    public String getPrefix() {
        return this.networkConfig.getMessageConfig().getPrefix().replace("&", "§");
    }

    /**
     * Returns instance of the CloudAPI
     * @return
     */
    public static CloudAPI getInstance() {
        if (instance == null) {
            instance = new CloudAPI();
        }
        return instance;
    }

    /**
     * Returns CloudProvided Scheduler
     * @return
     */
    public Scheduler getScheduler() {
        return this.cloudLibrary.getService(Scheduler.class);
    }

    /**
     * Returns statistics of Cloud by Query
     * @return
     */
    public Statistics getStatistics() {
        return CloudAPI.getInstance().sendQuery(new ResultPacketStatistics()).getResult();
    }

    /**
     * Iterates through all the permissions of a player
     * @param player > Name of the player
     * @param uuid > UUID of the player
     * @param ipAddress > IP of the player to set default Data if player not exists
     * @param accept > Consumer<String> that accepts all the permissions
     */
    public void updatePermissions(String player, UUID uuid, String ipAddress, Consumer<String> accept) {
        if (this.permissionPool == null || !this.permissionPool.isAvailable() ) {
            System.out.println("[CloudAPI] Couldn't update Permissions for " + player + " because PermissionPool is not available!");
            return;
        }
        this.permissionPool.checkFix(player);
        CloudPlayerData data = this.permissionPool.getPlayerDataOrDefault(player);

        List<PermissionEntry> entries = new LinkedList<>(data.getPermissionEntries());

        Value<Boolean> booleanValue = new Value<>(false);
        try {
            entries.forEach(permissionEntry -> {
                PermissionGroup permissionGroup = permissionPool.getPermissionGroupFromName(permissionEntry.getPermissionGroup());
                if (!this.permissionPool.isRankValid(player, permissionGroup)) {
                    booleanValue.setValue(true);
                    permissionPool.removePermissionGroup(player, permissionGroup);
                }
            });
        } catch (ConcurrentModificationException | UnsupportedOperationException e) {
            e.printStackTrace();
        }

        if (data.isDefault()) {
            data.setUuid(uuid);
            data.setIpAddress(ipAddress);
        }

        if (booleanValue.getValue()) {
            data.setPermissionEntries(entries);
            permissionPool.updatePlayerData(player, data);
            permissionPool.update();
        }
        List<String> permissions = new LinkedList<>();
        entries.forEach(entry -> {
            PermissionGroup group = permissionPool.getPermissionGroupFromName(entry.getPermissionGroup());
            if (group == null) {
                return;
            }
            permissions.addAll(group.getPermissions());
            group.getInheritances().forEach(i -> permissions.addAll(permissionPool.getPermissionGroupFromName(i).getPermissions()));
        });
        permissions.addAll(data.getPermissions());
        permissions.forEach(accept);
    }


    /**
     * Updates the current Service
     * @return current CloudAPI
     */
    public CloudAPI update() {
        if (this.getService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Can't update a ProxyService!");
        }
        CloudServer.getInstance().getManager().update();
        return this;
    }

    /**
     * Sets the State of the Service
     * @param serviceState
     * @return current CloudAPI
     */
    public CloudAPI setServiceState(ServiceState serviceState) {
        if (this.getService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Can't change ServiceState of a ProxyService!");
        }
        CloudServer.getInstance().getManager().setServiceState(serviceState);
        return this;
    }

    /**
     * Sets the max Players of this service
     * @param maxPlayers
     * @return current CloudAPI
     */
    public CloudAPI setMaxPlayers(int maxPlayers) {
        if (this.getService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Can't change maxPlayers of a ProxyService!");
        }
        CloudServer.getInstance().getManager().setMaxPlayers(maxPlayers);
        return this;
    }

    /**
     * Sets the MOTD of the current service
     * @param motd
     * @return current CloudAPI
     */
    public CloudAPI setMotd(String motd) {
        if (this.getService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Can't change MOTD of a ProxyService!");
        }
        CloudServer.getInstance().getManager().setMotd(motd);
        return this;
    }

    /**
     * Updates the Nametag for a player
     *
     * @param player
     * @param prefix
     * @param suffix
     * @param priority
     */
    public void updateNametag(CloudPlayer player, String prefix, String suffix, Integer priority) {
        CloudServer.getInstance().getNametagManager().setNametag(prefix, suffix, priority, player.getName());
    }

    @Override
    public CloudExecutor getCurrentExecutor() {
        return this.cloudClient;
    }

    @Override
    public CloudType getType() {
        return CloudType.CLOUDAPI;
    }

}
