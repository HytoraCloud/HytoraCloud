package de.lystx.hytoracloud.bridge.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.velocity.listener.cloud.CloudListener;
import de.lystx.hytoracloud.bridge.velocity.listener.player.*;
import de.lystx.hytoracloud.bridge.velocity.listener.other.ProxyPingListener;
import de.lystx.hytoracloud.bridge.velocity.listener.other.TablistListener;
import de.lystx.hytoracloud.bridge.velocity.listener.server.ServerConnectListener;
import de.lystx.hytoracloud.bridge.velocity.listener.server.ServerKickListener;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.driver.commons.chat.CloudComponent;
import de.lystx.hytoracloud.driver.commons.chat.CloudComponentAction;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;



import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

@Plugin(
        id = "hytora_proxy_bridge",
        name = "CloudBridge",
        version = "1.0",
        description = "This is the bridge between cloud and proxy",
        authors = "Lystx",
        url = "https://hytora.cloud"
)

@Getter
public class VelocityBridge {

    private final ProxyServer server;
    private final Logger logger;

    @Getter
    private static VelocityBridge instance;

    @Inject
    public VelocityBridge(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;

        CloudBridge.load();

        CloudDriver.getInstance().execute(() -> {
            instance = this;


            CloudBridge.getInstance().setProxyBridge(new ProxyBridge() {

                @Override
                public void updateTabList() {

                    for (Player player : server.getAllPlayers()) {

                        TabList tabList = CloudBridge.getInstance().loadRandomTablist();

                        if (!CloudDriver.getInstance().getProxyConfig().isEnabled() || !tabList.isEnabled()) {
                            return;
                        }

                        CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getUniqueId());

                        player.sendPlayerListHeaderAndFooter(
                                Component.text(Objects.requireNonNull(formatTabList(cloudPlayer, tabList.getHeader()))),
                                Component.text(Objects.requireNonNull(formatTabList(cloudPlayer, tabList.getFooter())))
                        );
                    }
                }

                @Override
                public NetworkHandler getNetworkHandler() {
                    return new CloudListener();
                }

                @Override
                public long getPing(UUID uniqueId) {

                    Player player = server.getPlayer(uniqueId).orElse(null);
                    if (player == null) {
                        return -1L;
                    }

                    return player.getPing();
                }

                @Override
                public void kickPlayer(UUID uniqueId, String reason) {

                    Player player = server.getPlayer(uniqueId).orElse(null);
                    if (player == null) {
                        return;
                    }

                    player.disconnect(Component.text(reason));
                }

                @Override
                public void connectPlayer(UUID uniqueId, String server) {

                    Player player = VelocityBridge.getInstance().getServer().getPlayer(uniqueId).orElse(null);
                    if (player == null) {
                        return;
                    }

                    RegisteredServer registeredServer = VelocityBridge.getInstance().getServer().getServer(server).orElse(null);
                    if (registeredServer == null) {
                        return;
                    }

                    player.createConnectionRequest(registeredServer).connect();
                }

                @Override
                public void fallbackPlayer(UUID uniqueId) {

                    Player player = server.getPlayer(uniqueId).orElse(null);
                    if (player == null) {
                        return;
                    }

                    CloudPlayer cloudPlayer = CloudPlayer.dummy(player.getUsername(), player.getUniqueId());
                    IService fallback = CloudDriver.getInstance().getFallback(cloudPlayer);

                    server.getServer(fallback.getName()).ifPresent(registeredServer -> player.createConnectionRequest(registeredServer).connect());

                }

                @Override
                public void messagePlayer(UUID uniqueId, String message) {

                    Player player = server.getPlayer(uniqueId).orElse(null);
                    if (player == null) {
                        return;
                    }

                    player.sendMessage(Component.text(message));
                }

                @Override
                public void sendComponent(UUID uniqueId, CloudComponent component) {

                    Player player = server.getPlayer(uniqueId).orElse(null);
                    if (player == null) {
                        return;
                    }

                    player.sendMessage(fromCloud(component));

                }

                @Override
                public void stopServer(IService IService) {

                    RegisteredServer registeredServer = server.getServer(IService.getName()).orElse(null);
                    if (registeredServer == null) {
                        return;
                    }
                    ServerInfo serverInfo = registeredServer.getServerInfo();

                    server.unregisterServer(serverInfo);
                }

                @Override
                public List<String> getAllServices() {
                    List<String> list = new LinkedList<>();
                    for (RegisteredServer allServer : server.getAllServers()) {
                        list.add(allServer.getServerInfo().getName());
                    }
                    return list;
                }

                @Override
                public Map<String, UUID> getPlayerInfos() {
                    Map<String, UUID> map = new HashMap<>();
                    for (Player allPlayer : server.getAllPlayers()) {
                        map.put(allPlayer.getUsername(), allPlayer.getUniqueId());
                    }
                    return map;
                }

                @Override
                public void registerService(IService IService) {

                    //Proxy's do not need to be registered
                    if (IService.getGroup().getType().isProxy()) {
                        return;
                    }

                    //Server not already registered
                    if (server.getServer(IService.getName()).orElse(null) == null) {
                        ServerInfo serverInfo = new ServerInfo(IService.getName(), new InetSocketAddress(IService.getHost(), IService.getPort()));
                        server.registerServer(serverInfo);

                    }


                }

                @Override
                public void removeServer(String s) {

                    RegisteredServer registeredServer = server.getServer(s).orElse(null);
                    if (registeredServer == null) {
                        return;
                    }
                    ServerInfo serverInfo = registeredServer.getServerInfo();

                    server.unregisterServer(serverInfo);
                }

                @Override
                public void stopProxy() {
                    server.shutdown();
                }

                @Override
                public ProxyVersion getVersion() {
                    return ProxyVersion.VELOCITY;
                }
            });


            this.bootstrap();


        });
    }



    /**
     * Creates a {@link Component} from a {@link CloudComponent}
     *
     * @param cloudComponent the cloudComponent
     * @return built md5 textComponent
     */
    private Component fromCloud(CloudComponent cloudComponent) {
        Component textComponent = Component.text(cloudComponent.getMessage());
        cloudComponent.getActions().forEach((action1, objects) -> {

            if (action1 != null && objects != null) {
                if (action1.equals(CloudComponentAction.CLICK_EVENT_RUN_COMMAND)) {

                    textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, (String) objects[0]));

                } else if (action1.equals(CloudComponentAction.CLICK_EVENT_OPEN_URL)) {
                    textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, (String) objects[0]));
                } else if (action1.equals(CloudComponentAction.CLICK_EVENT_SUGGEST_COMMAND)) {
                    textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, (String) objects[0]));
                } else if (action1.equals(CloudComponentAction.HOVER_EVENT_SHOW_ENTITY)) {

                } else {

                }

            }
        });
        cloudComponent.getCloudComponents().forEach(component -> textComponent.append(this.fromCloud(component)));
        return textComponent;
    }

    public void bootstrap() {

        //Registers all Listeners
        this.server.getEventManager().register(this, new CommandListener());
        this.server.getEventManager().register(this, new ProxyPingListener());
        this.server.getEventManager().register(this, new TablistListener());
        this.server.getEventManager().register(this, new PlayerLoginListener());
        this.server.getEventManager().register(this, new PlayerServerListener());
        this.server.getEventManager().register(this, new ServerKickListener());
        this.server.getEventManager().register(this, new ServerConnectListener());


        if (CloudDriver.getInstance().getProxyConfig() == null) {
            CloudDriver.getInstance().messageCloud(CloudDriver.getInstance().getThisService().getName(), "§cCouldn't find §eProxyConfig §cfor this service!");
            System.out.println("[CloudBridge] Couldn't find ProxyConfig!");
        }

    }

    public void shutdown() {
        try {
            CloudDriver.getInstance().getConnection().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
