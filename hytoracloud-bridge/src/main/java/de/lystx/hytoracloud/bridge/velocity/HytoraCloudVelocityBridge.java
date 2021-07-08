package de.lystx.hytoracloud.bridge.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.proxy.commands.*;
import de.lystx.hytoracloud.bridge.proxy.handler.*;
import de.lystx.hytoracloud.bridge.velocity.events.VelocityProxyChatEvent;
import de.lystx.hytoracloud.bridge.velocity.listener.cloud.CloudListener;
import de.lystx.hytoracloud.bridge.velocity.listener.player.*;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.messenger.ChannelMessageListener;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerInformation;
import io.thunder.Thunder;
import io.thunder.connection.ErrorHandler;
import io.thunder.packet.Packet;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

@Plugin(
        id = "hytora_proxy_bridge",
        name = "CloudBridge",
        version = "1.0",
        description = "This is the bridge between cloud and proxy",
        authors = "Lystx",
        url = "https://hytora.cloud"
)

@Getter
public class HytoraCloudVelocityBridge {

    private final ProxyServer server;
    private final Logger logger;

    @Getter
    private static HytoraCloudVelocityBridge instance;

    @Inject
    public HytoraCloudVelocityBridge(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;

        CloudBridge.load();

        CloudDriver.getInstance().execute(() -> {
            instance = this;

            this.bootstrap();

            CloudDriver.getInstance().registerNetworkHandler(new NetworkHandler() {
                @Override
                public void onServerStart(Service service) {
                    HytoraCloudVelocityBridge.this.notify(3, service.getName());
                }

                @Override
                public void onServerQueue(Service service) {
                    HytoraCloudVelocityBridge.this.notify(1, service.getName());
                }

                @Override
                public void onServerStop(Service service) {
                    HytoraCloudVelocityBridge.this.notify(2, service.getName());
                }
            });
        });
    }


    /**
     * Notifies all {@link com.velocitypowered.api.proxy.Player}s
     * on the Network if they have the permission to
     * get notified and if they have enabled it
     *
     * @param state the state
     * @param servername the name of the server
     */
    public void notify(int state, String servername) {
        for (Player player : this.server.getAllPlayers()) {
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
                    message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServerShutdownMessage().
                            replace("&", "§").
                            replace("%server%", servername).
                            replace("%prefix%", CloudDriver.getInstance().getCloudPrefix());
                    break;
                default:
                    message = "";
                    break;

            }
            player.sendMessage(Component.text(message));
        }
    }


    public void bootstrap() {

        CloudDriver.getInstance()
                //Registers all the PacketHandlers
                .registerPacketHandler(
                        new ProxyHandlerRegister(),
                        new ProxyHandlerUnregister(),
                        new ProxyHandlerConfig(),
                        new ProxyHandlerCloudPlayer(),
                        new ProxyHandlerShutdown());

        CloudDriver.getInstance().registerNetworkHandler(new CloudListener()); //Registers the NetworkHandler

        //Registers all Listeners
        this.server.getEventManager().register(this, new CommandListener());
        this.server.getEventManager().register(this, new PlayerLoginListener());
        this.server.getEventManager().register(this, new PlayerServerListener());
        this.server.getEventManager().register(this, new ServerKickListener());
        this.server.getEventManager().register(this, new ServerConnectListener());


        if (CloudDriver.getInstance().getProxyConfig() == null) {
            CloudDriver.getInstance().messageCloud(CloudDriver.getInstance().getThisService().getName(), "§cCouldn't find §eProxyConfig §cfor this service!");
            System.out.println("[CloudAPI] Couldn't find ProxyConfig!");
        }


        CloudDriver.getInstance().getChannelMessenger().registerChannelListener("hytoraCloud::player", new ChannelMessageListener() {
            @Override
            public void onReceiveMessage(String identifier, JsonEntity data, String[] targetComponents) {
                if (identifier.equalsIgnoreCase("chatMessage")) {
                    String player = data.getString("player");
                    String chatMessage = data.getString("message");
                    CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player);
                    if (cloudPlayer == null) {
                        return;
                    }
                    server.getEventManager().fire(new VelocityProxyChatEvent(cloudPlayer, chatMessage));
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
