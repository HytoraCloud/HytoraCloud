package de.lystx.cloudapi.proxy;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.command.*;
import de.lystx.cloudapi.proxy.handler.*;
import de.lystx.cloudapi.proxy.listener.network.CloudListener;
import de.lystx.cloudapi.proxy.listener.network.NetworkManager;
import de.lystx.cloudapi.proxy.listener.other.ProxyPingListener;
import de.lystx.cloudapi.proxy.listener.other.TablistListener;
import de.lystx.cloudapi.proxy.listener.player.CommandListener;
import de.lystx.cloudapi.proxy.listener.player.PlayerListener;
import de.lystx.cloudapi.proxy.listener.server.ServerConnectListener;
import de.lystx.cloudapi.proxy.listener.server.ServerKickListener;
import de.lystx.cloudsystem.library.elements.interfaces.CloudService;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudapi.proxy.manager.HubManager;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.util.Action;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

@Getter
public class CloudProxy extends Plugin implements CloudService {

    @Getter
    private static CloudProxy instance;

    private HubManager hubManager;
    private NetworkManager networkManager;
    private Action action;

    @Override
    public void onEnable() {
        CloudAPI.getInstance().execute(() -> {
            instance = this;

            this.action = new Action();
            this.hubManager = new HubManager();
            this.networkManager = new NetworkManager();

            Constants.SERVICE_TYPE = ServiceType.PROXY;
            this.bootstrap();
        });
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

        CloudAPI.getInstance()
                //Registers all the PacketHandlers
                .registerPacketHandler(
                    new PacketHandlerProxyStartServer(),
                    new PacketHandlerProxyStopServer(CloudAPI.getInstance()),
                    new PacketHandlerProxyConfig(CloudAPI.getInstance()),
                    new PacketHandlerProxyCloudPlayerHandler(CloudAPI.getInstance()),
                    new PacketHandlerProxyStop(CloudAPI.getInstance()),
                    new PacketHandlerProxyChatEvent(CloudAPI.getInstance()),
                    new PacketHandlerProxyEvent(CloudAPI.getInstance()))

                .registerNetworkHandler(new CloudListener()) //Registers the NetworkHandler

                //Registers all Commands
                .registerCommand(new CloudCommand())
                .registerCommand(new HubCommand())
                .registerCommand(new ListCommand())
                .registerCommand(new WhereCommands())
                .registerCommand(new PermsCommand())
                .registerCommand(new NetworkCommand());

        //Registers all Listeners
        this.getProxy().getPluginManager().registerListener(this, new ProxyPingListener());
        this.getProxy().getPluginManager().registerListener(this, new TablistListener());
        this.getProxy().getPluginManager().registerListener(this, new CommandListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerKickListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerConnectListener());


        if (this.getProxyConfig() == null) {
            CloudAPI.getInstance().messageCloud(CloudAPI.getInstance().getService().getName(), "§cCouldn't find §eProxyConfig §cfor this service!");
            System.out.println("[CloudAPI] Couldn't find ProxyConfig!");
        }
        System.out.println("[CloudProxy] Booted up in " + this.action.time() + "ms");

    }

    @Override
    public void shutdown() {
        if (CloudAPI.getInstance().getCloudClient().isConnected()) {
            CloudAPI.getInstance().getCloudClient().disconnect();
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
