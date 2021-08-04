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
import de.lystx.hytoracloud.driver.service.bridge.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.player.*;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.other.ProxyPingListener;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.server.ServerConnectListener;
import de.lystx.hytoracloud.bridge.proxy.velocity.listener.server.ServerKickListener;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.bridge.proxy.ProxyBridge;
import de.lystx.hytoracloud.driver.player.featured.IPlayerSettings;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.service.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.service.minecraft.chat.CloudComponentAction;
import de.lystx.hytoracloud.driver.utils.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;


import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.wrapped.PlayerSettingsObject;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.slf4j.Logger;

import java.io.IOException;
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
                public IPlayerSettings getSettings(UUID uniqueId) {

                    Player player = server.getPlayer(uniqueId).orElse(null);
                    if (player == null) {
                        return null;
                    }
                    return new PlayerSettingsObject(
                            player.getPlayerSettings().getLocale(),
                            player.getPlayerSettings().hasChatColors(),
                            player.getPlayerSettings().getViewDistance(),
                            player.getPlayerSettings().getSkinParts().hasHat(),
                            player.getPlayerSettings().getSkinParts().hasJacket(),
                            player.getPlayerSettings().getSkinParts().hasRightSleeve(),
                            player.getPlayerSettings().getSkinParts().hasLeftSleeve(),
                            player.getPlayerSettings().getSkinParts().hasRightPants(),
                            player.getPlayerSettings().getSkinParts().hasLeftPants(),
                            player.getPlayerSettings().getSkinParts().hasCape(),
                            IPlayerSettings.ChatMode.valueOf(player.getPlayerSettings().getChatMode().name()),
                            IPlayerSettings.MainHand.valueOf(player.getPlayerSettings().getMainHand().name())
                    );
                }

                @Override
                public int getPing(UUID uniqueId) {

                    Player player = server.getPlayer(uniqueId).orElse(null);
                    if (player == null) {
                        return -1;
                    }

                    return (int) player.getPing();
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
                    if (service.getGroup().getEnvironment() == ServerEnvironment.PROXY) {
                        return;
                    }

                    //Server not already registered
                    if (server.getServer(service.getName()).orElse(null) == null) {
                        ServerInfo serverInfo = new ServerInfo(service.getName(), service.getAddress());
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
        this.server.getEventManager().register(this, new PlayerLoginListener());
        this.server.getEventManager().register(this, new PlayerServerListener());
        this.server.getEventManager().register(this, new ServerKickListener());
        this.server.getEventManager().register(this, new ServerConnectListener());
        this.server.getEventManager().register(this, new PlayerInjectListener());

    }

    @Override
    public int getPing(UUID playerUniqueId) {
        Player player = server.getPlayer(playerUniqueId).orElse(null);
        if (player == null) {
            return -1;
        }

        return (int) player.getPing();
    }

    @Override
    public void sendMessage(UUID uniqueId, ChatComponent message) {
        Player player = server.getPlayer(uniqueId).orElse(null);
        if (player == null) {
            return;
        }

        player.sendMessage(fromCloud(message));
    }

    @Override
    public void flushCommand(String command) {
        server.getCommandManager().executeImmediatelyAsync(server.getConsoleCommandSource(), command);
    }

    @Override
    public PropertyObject requestProperties() {

        IService service = CloudDriver.getInstance().getServiceManager().getThisService();
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
                        .append("online-mode", CloudDriver.getInstance().getConfigManager().getProxyConfig().isOnlineMode())
        );

        List<JsonObject<?>> plugins = new LinkedList<>();

        for (PluginContainer plugin : server.getPluginManager().getPlugins()) {
            PluginDescription desc = plugin.getDescription();

            List<String> dependencies = new LinkedList<>();

            for (PluginDependency dependency : desc.getDependencies()) {
                dependencies.add(dependency.getId());
            }

            plugins.add(
                    JsonObject.serializable()
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
    public ServerEnvironment type() {
        return ServerEnvironment.PROXY;
    }


    @Override
    public void sendTabList(UUID uniqueId, String header, String footer) {
        Player player = server.getPlayer(uniqueId).orElse(null);

        if (player == null) {
            return;
        }

        player.sendPlayerListHeaderAndFooter(Component.text(header), Component.text(footer));
    }

    @Override
    public void shutdown() {
        try {
            CloudDriver.getInstance().getConnection().shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
