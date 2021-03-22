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

    private HubManager hubManager;
    private NetworkManager networkManager;

    @Override
    public void onEnable() {
        instance = this;

        this.hubManager = new HubManager();
        this.networkManager = new NetworkManager();

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
        return CloudAPI.getInstance().getService().getServiceGroup().getValues().get("proxyConfig", ProxyConfig.class);
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

        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerProxyStartServer(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerProxyStopServer(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerProxyConfig(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerProxyCloudPlayerHandler(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerProxyStop(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerProxyChatEvent(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerPacketHandler(new PacketHandlerProxyEvent(CloudAPI.getInstance()));
        CloudAPI.getInstance().getCloudClient().registerHandler(new CloudListener());

        this.getProxy().getPluginManager().registerListener(this, new ProxyPingListener());
        this.getProxy().getPluginManager().registerListener(this, new TablistListener());
        this.getProxy().getPluginManager().registerListener(this, new CommandListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerKickListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerConnectListener());

        CloudAPI.getInstance().registerCommand(new CloudCommand());
        CloudAPI.getInstance().registerCommand(new HubCommand());
        CloudAPI.getInstance().registerCommand(new ListCommand());
        CloudAPI.getInstance().registerCommand(new WhereCommands());
        CloudAPI.getInstance().registerCommand(new PermsCommand());
        CloudAPI.getInstance().registerCommand(new NetworkCommand());

        if (this.getProxyConfig() == null) {
            CloudAPI.getInstance().messageCloud(CloudAPI.getInstance().getService().getName(), "§cCouldn't find §eProxyConfig §cfor this service!");
            System.out.println("[CloudAPI] Couldn't find ProxyConfig!");
        }

    }

    @Override
    public void shutdown() {
        if (CloudAPI.getInstance().getCloudClient().isConnected()) {
            CloudAPI.getInstance().disconnect();
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        CloudAPI.getInstance().sendPacket(packet);
    }

    @Override
    public CloudExecutor getCurrentExecutor() {
        return CloudAPI.getInstance().getCurrentExecutor();
    }

    @Override
    public CloudType getType() {
        return CloudAPI.getInstance().getType();
    }
}
