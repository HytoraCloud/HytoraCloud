package de.lystx.hytoracloud.bridge.proxy;

import de.lystx.hytoracloud.bridge.CloudBridge;
//import de.lystx.bridge.proxy.commands.*;
import de.lystx.hytoracloud.bridge.proxy.commands.CloudCommand;
import de.lystx.hytoracloud.bridge.proxy.events.player.ProxyServerChatEvent;
//import de.lystx.bridge.proxy.handler.*;
//import de.lystx.bridge.proxy.impl.commands.*;
import de.lystx.hytoracloud.bridge.proxy.handler.*;
import de.lystx.hytoracloud.bridge.proxy.impl.commands.*;
import de.lystx.hytoracloud.bridge.proxy.impl.listener.network.CloudListener;
import de.lystx.hytoracloud.bridge.proxy.impl.listener.other.ProxyPingListener;
import de.lystx.hytoracloud.bridge.proxy.impl.listener.other.TablistListener;
import de.lystx.hytoracloud.bridge.proxy.impl.listener.player.CommandListener;
import de.lystx.hytoracloud.bridge.proxy.impl.listener.player.PlayerListener;
import de.lystx.hytoracloud.bridge.proxy.impl.listener.server.ServerConnectListener;
import de.lystx.hytoracloud.bridge.proxy.impl.listener.server.ServerKickListener;
import de.lystx.hytoracloud.bridge.proxy.manager.HubManager;
import de.lystx.hytoracloud.driver.elements.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.messenger.ChannelMessageListener;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerInformation;
import io.thunder.Thunder;
import io.thunder.connection.ErrorHandler;
import io.thunder.packet.Packet;
import de.lystx.hytoracloud.driver.service.util.other.Action;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

@Getter
public class CloudProxy extends Plugin{

    @Getter
    private static CloudProxy instance;

    private HubManager hubManager;
    private Action action;

    @Override
    public void onEnable() {
        CloudBridge.load();

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
                if (s.equalsIgnoreCase("de.lystx.hytoracloud.module.serverselector.packets.PacketOutServerSelector")) {
                    return;
                }
                System.out.println("[CloudAPI] A §ePacket §fcould §cnot §fbe decoded (§b" + s + "§f)");
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });

        CloudDriver.getInstance().execute(() -> {
            instance = this;

            this.action = new Action();
            this.hubManager = new HubManager();


            ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPingListener());
            ProxyServer.getInstance().getPluginManager().registerListener(this, new TablistListener());

            CloudDriver.getInstance().registerCommand(new PermsCommand());
            CloudDriver.getInstance().registerCommand(new CloudCommand());
            CloudDriver.getInstance().registerCommand(new HubCommand());
            CloudDriver.getInstance().registerCommand(new WhereAmICommand());
            CloudDriver.getInstance().registerCommand(new WhereIsCommand());
            CloudDriver.getInstance().registerCommand(new ListCommand());
            CloudDriver.getInstance().registerCommand(new NetworkCommand());

            this.bootstrap();

            CloudDriver.getInstance().registerNetworkHandler(new NetworkHandler() {
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
            if (!CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), "cloudsystem.notify")) {
                return;
            }
            PlayerInformation playerData = CloudDriver.getInstance().getPermissionPool().getPlayerInformation(player.getUniqueId());
            if (playerData != null && !playerData.isNotifyServerStart()) {
                return;
            }
            String message = null;
            switch (state){
                case 1:
                    message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServerStartMessage().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudDriver.getInstance().getCloudPrefix());
                    break;
                case 2:
                    message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServerStopMessage().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudDriver.getInstance().getCloudPrefix());
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

    public void bootstrap() {

        CloudDriver.getInstance()
                //Registers all the PacketHandlers
                .registerPacketHandler(
                        new PacketHandlerProxyStartServer(),
                        new PacketHandlerProxyStopServer(),
                        new PacketHandlerProxyConfig(),
                        new PacketHandlerProxyCloudPlayerHandler(),
                        new PacketHandlerProxyStop(),
                        new PacketHandlerProxyEvent());

        CloudDriver.getInstance().registerNetworkHandler(new CloudListener()); //Registers the NetworkHandler

        //Registers all Listeners
        this.getProxy().getPluginManager().registerListener(this, new CommandListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerKickListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerConnectListener());


        if (CloudDriver.getInstance().getProxyConfig() == null) {
            CloudDriver.getInstance().messageCloud(CloudDriver.getInstance().getThisService().getName(), "§cCouldn't find §eProxyConfig §cfor this service!");
            System.out.println("[CloudAPI] Couldn't find ProxyConfig!");
        }
        System.out.println("[CloudProxy] Booted up in " + this.action.time() + "ms");


        CloudDriver.getInstance().getChannelMessenger().registerChannelListener("hytoraCloud::player", new ChannelMessageListener() {
            @Override
            public void onReceiveMessage(String identifier, JsonBuilder data, String[] targetComponents) {
                if (identifier.equalsIgnoreCase("chatMessage")) {
                    String player = data.getString("player");
                    String chatMessage = data.getString("message");
                    CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player);
                    if (cloudPlayer == null) {
                        return;
                    }
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerChatEvent(cloudPlayer, chatMessage));
                }
            }
        });

    }

    public void shutdown() {
        if (CloudDriver.getInstance().getConnection().isConnected()) {
            CloudDriver.getInstance().getConnection().disconnect();
        }
    }

}
