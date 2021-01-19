package de.lystx.cloudapi;


import de.lystx.cloudapi.standalone.handler.*;
import de.lystx.cloudapi.standalone.manager.CloudNetwork;
import de.lystx.cloudapi.standalone.manager.CloudPlayers;
import de.lystx.cloudapi.standalone.manager.Templates;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInCommand;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInLog;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.stats.Statistics;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;

@Getter @Setter
public class CloudAPI {

    private static CloudAPI instance;

    private NetworkConfig networkConfig;
    private PermissionPool permissionPool;
    private Statistics statistics;

    private final CloudLibrary cloudLibrary;
    private final CloudClient cloudClient;
    private final CloudNetwork network;
    private final Templates templates;
    private final CloudPlayers cloudPlayers;

    private boolean nametags;
    private boolean useChat;
    private boolean joinable;
    private String chatFormat;

    public CloudAPI() {
        instance = this;
        this.cloudLibrary = new CloudLibrary();
        this.cloudClient =  this.cloudLibrary.getCloudClient();

        this.network = new CloudNetwork(this);
        this.cloudPlayers = new CloudPlayers(this);
        this.permissionPool = new PermissionPool();
        this.templates = new Templates(this);
        this.statistics = new Statistics();

        this.chatFormat = "%prefix%%player% §8» §7%message%";
        this.useChat = false;
        this.nametags = false;
        this.joinable = false;

        this.cloudClient.registerPacketHandler(new PacketHandlerConfig(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerServices(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerCloudPlayers(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerPermissionPool(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerNetwork(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerSubChannel(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerStatistics(this));
        this.cloudClient.registerPacketHandler(new PacketHandlerCommunication(this));

        this.cloudClient.connect();
    }


    public void shutdown() {
        this.cloudClient.sendPacket(new PacketPlayInStopServer(this.getService()));
        this.cloudClient.disconnect();
    }

    public void sendCommand(String command) {
        this.cloudClient.sendPacket(new PacketPlayInCommand(command));
    }


    public void sendPacket(Packet packet) {
        this.cloudClient.sendPacket(packet);
    }

    public void messageCloud(String prefix, String message, boolean showUpInConsole) {
        this.cloudClient.sendPacket(new PacketPlayInLog(prefix, message, showUpInConsole));
    }

    public void messageCloud(String prefix, Object message) {
        this.messageCloud(prefix, String.valueOf(message), true);
    }

    public Service getService() {
        return this.getDocument().getObject(this.getDocument().getJsonObject(), Service.class);
    }

    public Document getProperties() {
        return this.getService().getProperties();
    }

    public Document getDocument() {
        return Document.fromFile(new File("./CLOUD/connection.json"));
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
}
