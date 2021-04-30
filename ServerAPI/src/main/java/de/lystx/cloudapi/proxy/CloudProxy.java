package de.lystx.cloudapi.proxy;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.handler.*;
import de.lystx.cloudapi.proxy.listener.other.TablistListener;
import de.lystx.cloudapi.proxy.listener.network.CloudListener;
import de.lystx.cloudapi.proxy.listener.player.CommandListener;
import de.lystx.cloudapi.proxy.listener.player.PlayerListener;
import de.lystx.cloudapi.proxy.listener.server.ServerConnectListener;
import de.lystx.cloudapi.proxy.listener.server.ServerKickListener;
import de.lystx.cloudsystem.library.elements.interfaces.CloudService;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudapi.proxy.manager.HubManager;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.util.Action;
import de.lystx.cloudsystem.library.service.util.CloudCache;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

@Getter
public class CloudProxy extends Plugin implements CloudService {

    @Getter
    private static CloudProxy instance;

    private HubManager hubManager;
    private Action action;

    @Override
    public void onEnable() {
        CloudAPI.getInstance().execute(() -> {
            instance = this;

            this.action = new Action();
            this.hubManager = new HubManager();


            ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPingListener());
            ProxyServer.getInstance().getPluginManager().registerListener(this, new TablistListener());

            CloudAPI.getInstance().registerCommand(new PermsCommand());
            CloudAPI.getInstance().registerCommand(new CloudCommand());
            CloudAPI.getInstance().registerCommand(new HubCommand());
            CloudAPI.getInstance().registerCommand(new WhereAmICommand());
            CloudAPI.getInstance().registerCommand(new WhereIsCommand());
            CloudAPI.getInstance().registerCommand(new ListCommand());
            CloudAPI.getInstance().registerCommand(new NetworkCommand());

            CloudCache.getInstance().setCurrentServiceType(ServiceType.PROXY);
            this.bootstrap();

            CloudAPI.getInstance().registerNetworkHandler(new NetworkHandler() {
                @Override
                public void onServerStart(Service service) {
                    CloudProxy.this.notify(3, service.getName());
                }

                @Override
                public void onServerQueue(Service service) {
                    CloudProxy.this.notify(1, service.getName());
                }

                @Override
                public void onServerStop(Service service) {
                    CloudProxy.this.notify(2, service.getName());
                }
            });
        });
    }



    /**
     * Notifies all {@link ProxiedPlayer}s
     * on the Network if they have the permission to
     * get notified and if they have enabled it
     *
     * @param state
     * @param servername
     */
    public void notify(int state, String servername) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!CloudAPI.getInstance().getPermissionPool().hasPermission(player.getName(), "cloudsystem.notify")) {
                return;
            }
            CloudPlayerData playerData = CloudAPI.getInstance().getPermissionPool().getPlayerData(player.getName());
            if (playerData != null && !playerData.isNotifyServerStart()) {
                return;
            }
            String message = null;
            switch (state){
                case 1:
                    message = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getServerStartMessage().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudAPI.getInstance().getPrefix());
                    break;
                case 2:
                    message = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getServerStopMessage().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudAPI.getInstance().getPrefix());
                    break;
                case 3:
                    return;

            }
            player.sendMessage(new TextComponent(message));
        }
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
        return CloudAPI.getInstance().getProxyConfig();
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

                .registerNetworkHandler(new CloudListener()); //Registers the NetworkHandler


        //Registers all Listeners
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
