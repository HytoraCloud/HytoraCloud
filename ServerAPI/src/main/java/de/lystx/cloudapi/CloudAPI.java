package de.lystx.cloudapi;


import de.lystx.cloudapi.standalone.handler.*;
import de.lystx.cloudapi.standalone.manager.CloudNetwork;
import de.lystx.cloudapi.standalone.manager.CloudPlayers;
import de.lystx.cloudapi.standalone.manager.Templates;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.CustomPacket;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCallEvent;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInCommand;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInLog;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInRegister;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.packets.result.Result;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.elements.packets.result.other.ResultPacketStatistics;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.stats.Statistics;
import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.lib.Repository;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.network.netty.NettyClient;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.util.Constants;
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
import java.util.function.Consumer;

@Getter @Setter
public class CloudAPI {

    private static CloudAPI instance;

    private NetworkConfig networkConfig;
    private PermissionPool permissionPool;

    private final CloudLibrary cloudLibrary;
    private final CloudClient cloudClient;
    private final CloudNetwork network;
    private final Templates templates;
    private final CloudPlayers cloudPlayers;
    private final ExecutorService executorService;
    private final CommandService commandService;

    private boolean nametags;
    private boolean useChat;
    private boolean joinable;
    private String chatFormat;

    public CloudAPI() {
        instance = this;
        this.cloudLibrary = new CloudLibrary(CloudLibrary.Type.CLOUDAPI);
        this.cloudClient =  this.cloudLibrary.getCloudClient();
        this.executorService = Executors.newCachedThreadPool();

        this.network = new CloudNetwork(this);
        this.cloudPlayers = new CloudPlayers(this);
        this.permissionPool = new PermissionPool(cloudLibrary);
        this.templates = new Templates(this);
        this.commandService = new CommandService(this.cloudLibrary, "Command", CloudService.Type.MANAGING);

        Constants.EXECUTOR = this.cloudClient;
        Constants.PERMISSION_POOL = this.permissionPool;

        this.chatFormat = "%prefix%%player% §8» §7%message%";
        this.useChat = false;
        this.nametags = false;
        this.joinable = false;

        this.cloudClient.registerPacketHandler(new PacketHandlerConfig(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerCommand(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerNetwork(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerSubChannel(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerCommunication(this));

        Thread cloudClient = new Thread(() -> {
            try {
                this.cloudClient.onConnectionEstablish(new Consumer<NettyClient>() {
                    @Override
                    public void accept(NettyClient nettyClient) {
                        nettyClient.sendPacket(new PacketPlayInRegister(getService()));
                    }
                });
                this.cloudClient.connect(this.getService().getHost(), this.getService().getCloudPort());
            } catch (Exception e) {
                System.out.println("[CLOUDAPI] Couldn't connect to CloudSystem! Stopping...");
                e.printStackTrace();
                System.exit(0);
            }
        }, "hytoraCloud_cloudAPI");

        cloudClient.start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "shutdown_hook"));
    }

    public void registerCommand(Object commandObject) {
        this.commandService.registerCommand(commandObject);
    }

    public void unregisterCommand(Object commandObject) {
        this.commandService.unregisterCommand(commandObject);
    }

    public void installMaven(String groupId, String artifactId, String version, Repository repo) {
        this.cloudLibrary.getLibraryService().install(groupId, artifactId, version, repo);
    }

    public void disconnect() {
        this.cloudClient.disconnect();
    }

    public void executeAsyncQuery(ResultPacket resultPacket, Consumer<Result> consumer) {
        this.executorService.execute(() -> {
            this.sendQuery(resultPacket).onResultSet(consumer);
        });
    }

    public Result sendQuery(ResultPacket packet) {
        Value<Result> value = new Value<>();
        UUID uuid = UUID.randomUUID();

        this.cloudClient.registerPacketHandler(new PacketHandlerAdapter() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof ResultPacket) {
                    ResultPacket resultPacket = (ResultPacket)packet;
                    if (uuid.equals(resultPacket.getUniqueId())) {
                        value.set(resultPacket.getResult());
                        cloudClient.getPacketAdapter().unregisterAdapter(this);
                    }
                }
            }
        });
        this.sendPacket(packet.uuid(uuid));
        int count = 0;

        while (value.get() == null && count++ < 3000) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (count >= 2999) {
            Result r = new Result(uuid, new VsonObject());
            r.setError(true);
            value.set(r);
        }
        return value.get();
    }

    public void shutdown() {
        this.sendPacket(new PacketPlayInStopServer(this.getService()));
        this.disconnect();
    }

    public void sendCommand(String command) {
        this.sendPacket(new PacketPlayInCommand(command));
    }

    public void sendPacket(Packet packet) {
        if (packet.getClass().getName().toLowerCase().contains("de.lystx.cloudsystem.library.elements.packets")) {
            this.cloudClient.sendPacket(packet);
        } else {
            this.cloudClient.sendPacket(new CustomPacket(packet));
        }
    }

    public void callEvent(Class<? extends Event> eventClass, Object... parameters) {
        this.sendPacket(new PacketCallEvent(eventClass, parameters));
    }

    public void messageCloud(String prefix, String message, boolean showUpInConsole) {
        this.sendPacket(new PacketPlayInLog(prefix, message, showUpInConsole));
    }

    public void messageCloud(String prefix, Object message) {
        this.messageCloud(prefix, String.valueOf(message), true);
    }

    public Service getService() {
        return this.getDocument().getAs(Service.class);
    }

    public SerializableDocument getProperties() {
        return this.getService().getProperties();
    }

    public VsonObject getDocument() {
        try {
            return new VsonObject(new File("./CLOUD/connection.json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPrefix() {
        return this.networkConfig.getMessageConfig().getPrefix().replace("&", "§");
    }

    public static CloudAPI getInstance() {
        return instance;
    }

    public Scheduler getScheduler() {
        return this.cloudLibrary.getService(Scheduler.class);
    }

    public Statistics getStatistics() {
        Statistics statistics = new Statistics();
        statistics.load(CloudAPI.getInstance().sendQuery(new ResultPacketStatistics()).getDocument());
        return statistics;
    }

    public void updatePermissions(String player, UUID uuid, String ipAddress, Consumer<String> accept) {
        this.permissionPool.checkFix(player);
        CloudPlayerData data = this.permissionPool.getPlayerDataOrDefault(player);
        List<PermissionEntry> entries = data.getPermissionEntries();

        boolean changed = false;
        try {
            for (PermissionEntry permissionEntry : entries) {
                PermissionGroup permissionGroup = permissionPool.getPermissionGroupFromName(permissionEntry.getPermissionGroup());
                if (!this.permissionPool.isRankValid(player, permissionGroup)) {
                    changed = true;
                    permissionPool.removePermissionGroup(player, permissionGroup);
                }
            }
        } catch (ConcurrentModificationException e) {}

        if (data.isDefault()) {
            data.setUuid(uuid);
            data.setIpAddress(ipAddress);
        }
        if (changed) {
            data.setPermissionEntries(entries);
            permissionPool.updatePlayerData(player, data);
            permissionPool.update();
        }
        List<String> permissions = new LinkedList<>();
        for (PermissionEntry entry : entries) {
            PermissionGroup group = permissionPool.getPermissionGroupFromName(entry.getPermissionGroup());
            if (group == null) {
                continue;
            }
            permissions.addAll(group.getPermissions());
            for (String i : group.getInheritances()) {
                permissions.addAll(permissionPool.getPermissionGroupFromName(i).getPermissions());
            }
        }
        permissions.addAll(data.getPermissions());
        for (String permission : permissions) {
            accept.accept(permission);
        }
    }
}
