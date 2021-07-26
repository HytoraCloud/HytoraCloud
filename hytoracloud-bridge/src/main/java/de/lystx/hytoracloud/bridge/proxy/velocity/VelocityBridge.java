package de.lystx.hytoracloud.bridge.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.lystx.hytoracloud.driver.bridge.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.cloud.CloudListener;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.player.*;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.other.ProxyPingListener;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.other.TablistListener;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.server.ServerConnectListener;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.server.ServerKickListener;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.bridge.ProxyBridge;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.CloudComponentAction;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;


import de.lystx.hytoracloud.driver.commons.service.PropertyObject;
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
public class VelocityBridge implements BridgeInstance {

    private final ProxyServer server;
    private final Logger logger;

    @Getter
    private static VelocityBridge instance;

    @Inject
    public VelocityBridge(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;

        CloudBridge.load(this);

        CloudDriver.getInstance().getExecutorService().execute(() -> {
            instance = this;


            CloudBridge.getInstance().setProxyBridge(new ProxyBridge() {

                @Override
                public void updateTabList() {

                    for (Player player : server.getAllPlayers()) {

                        TabList tabList = CloudBridge.getInstance().loadRandomTablist();

                        if (!CloudDriver.getInstance().getProxyConfig().isEnabled() || !tabList.isEnabled()) {
                            return;
                        }

                        ICloudPlayer ICloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUniqueId());

                        player.sendPlayerListHeaderAndFooter(
                                Component.text(Objects.requireNonNull(formatTabList(ICloudPlayer, tabList.getHeader()))),
                                Component.text(Objects.requireNonNull(formatTabList(ICloudPlayer, tabList.getFooter())))
                        );
                    }
                }

                @Override
                public String loadMotd() {
                    Motd motd = CloudBridge.getInstance().loadRandomMotd();
                    return motd.getFirstLine() + "\n" + motd.getSecondLine();
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

                    ICloudPlayer iCloudPlayer = ICloudPlayer.dummy(player.getUsername(), player.getUniqueId());
                    IService fallback = CloudDriver.getInstance().getFallbackManager().getFallback(iCloudPlayer);

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
                public void sendComponent(UUID uniqueId, ChatComponent component) {

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
                public void registerService(IService service) {

                    //Proxy's do not need to be registered
                    if (service.getGroup().getType().isProxy()) {
                        return;
                    }

                    //Server not already registered
                    if (server.getServer(service.getName()).orElse(null) == null) {
                        ServerInfo serverInfo = new ServerInfo(service.getName(), new InetSocketAddress(service.getHost(), service.getPort()));
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
     * Creates a {@link Component} from a {@link ChatComponent}
     *
     * @param chatComponent the cloudComponent
     * @return built md5 textComponent
     */
    private Component fromCloud(ChatComponent chatComponent) {
        Component textComponent = Component.text(chatComponent.getMessage());
        chatComponent.getActions().forEach((action1, objects) -> {

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
        chatComponent.getChatComponents().forEach(component -> textComponent.append(this.fromCloud(component)));
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
        this.server.getEventManager().register(this, new IpInjector());

    }

    @Override
    public void flushCommand(String command) {
        server.getCommandManager().execute(server.getConsoleCommandSource(), command);
    }

    @Override
    public PropertyObject requestProperties() {

        IService service = CloudDriver.getInstance().getCurrentService();
        PropertyObject propertyObject = new PropertyObject();

        propertyObject.append("bungeeCord",
                new PropertyObject()
                        .append("version", server.getVersion().getName())
                        .append("address", server.getBoundAddress())
        );

        propertyObject.append("service",
                new PropertyObject()
                        .append("max-players", service.getGroup().getMaxPlayers())
                        .append("players", server.getAllPlayers().size())
                        .append("online-mode", CloudDriver.getInstance().getProxyConfig().isOnlineMode())
        );

        List<PropertyObject> plugins = new LinkedList<>();

        for (PluginContainer plugin : server.getPluginManager().getPlugins()) {
            PluginDescription desc = plugin.getDescription();

            List<String> dependencies = new LinkedList<>();

            for (PluginDependency dependency : desc.getDependencies()) {
                dependencies.add(dependency.getId());
            }

            plugins.add(
                    new PropertyObject()
                            .append("name", desc.getName().orElse("None"))
                            .append("website", desc.getUrl())
                            .append("commands", new LinkedList<>())
                            .append("description", desc.getDescription().orElse("None"))
                            .append("version", desc.getVersion().orElse("None"))
                            .append("authors", desc.getAuthors())
                            .append("dependencies", dependencies)
                            .append("soft-dependencies", new LinkedList<>())
                            .append("main-class", desc.getSource().orElse(null) == null ? "None" : desc.getSource().get().toString())
            );

        }

        propertyObject.append("plugins", plugins);

        return propertyObject;
    }

    @Override
    public void sendTabList(UUID uniqueId, ChatComponent header, ChatComponent footer) {
        Player player = server.getPlayer(uniqueId).orElse(null);

        if (player == null) {
            return;
        }

        player.sendPlayerListHeaderAndFooter(fromCloud(header), fromCloud(footer));
    }

    @Override
    public void shutdown() {
        try {
            CloudDriver.getInstance().getConnection().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
