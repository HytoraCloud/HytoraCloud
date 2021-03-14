package de.lystx.cloudapi.proxy;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.command.*;
import de.lystx.cloudapi.proxy.handler.*;
import de.lystx.cloudsystem.library.elements.interfaces.CloudService;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudapi.proxy.listener.*;
import de.lystx.cloudapi.proxy.manager.HubManager;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

@Getter
public class CloudProxy extends Plugin implements CloudService {

    @Getter
    private static CloudProxy instance;

    private CloudAPI cloudAPI;
    private HubManager hubManager;
    private NetworkManager networkManager;
    private List<Service> services;

    @Override
    public void onEnable() {
        instance = this;

        this.cloudAPI = new CloudAPI();
        this.hubManager = new HubManager();
        this.networkManager = new NetworkManager();
        this.services = new LinkedList<>();

        this.bootstrap();
    }

    @Override
    public void onDisable() {
        this.shutdown();
    }

    /**
     * Executes a Command as Console
     * @param line
     */
    public void executeCommand(String line) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), line);
    }

    /**
     * Returns {@link ProxyConfig} from VsonObject
     * see {@link CloudAPI#getService()}
     * @return
     */
    public ProxyConfig getProxyConfig() {
        return this.cloudAPI.getService().getServiceGroup().getValues().get("proxyConfig", ProxyConfig.class);
    }

    /**
     * Returns the current ProxyPort
     * it iterates through all listeners
     * @return Integer
     */
    public int getProxyPort() {
        for (ListenerInfo listener : ProxyServer.getInstance().getConfig().getListeners()) {
            return listener.getHost().getPort();
        }
        return -1;
    }

    @Override
    public void bootstrap() {

        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyStartServer(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyStopServer(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyConfig(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyCloudPlayerHandler(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyStop(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyChatEvent(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyEvent(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerHandler(new CloudListener());

        this.getProxy().getPluginManager().registerListener(this, new ProxyPingListener());
        this.getProxy().getPluginManager().registerListener(this, new TablistListener());
        this.getProxy().getPluginManager().registerListener(this, new CommandListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerKickListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerConnectListener());

        this.cloudAPI.registerCommand(new CloudCommand());
        this.cloudAPI.registerCommand(new HubCommand());
        this.cloudAPI.registerCommand(new ListCommand());
        this.cloudAPI.registerCommand(new WhereCommands());
        this.cloudAPI.registerCommand(new PermsCommand());
        this.cloudAPI.registerCommand(new NetworkCommand());

        if (this.getProxyConfig() == null) {
            this.cloudAPI.messageCloud(this.cloudAPI.getService().getName(), "§cCouldn't find §eProxyConfig §cfor this service!");
            System.out.println("[CloudAPI] Couldn't find ProxyConfig!");
        }

    }

    @Override
    public void shutdown() {
        if (this.cloudAPI.getCloudClient().isConnected()) {
            this.cloudAPI.disconnect();
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        this.cloudAPI.sendPacket(packet);
    }

    @Override
    public CloudExecutor getCurrentExecutor() {
        return this.cloudAPI.getCurrentExecutor();
    }

    @Override
    public CloudType getType() {
        return this.cloudAPI.getType();
    }
}
