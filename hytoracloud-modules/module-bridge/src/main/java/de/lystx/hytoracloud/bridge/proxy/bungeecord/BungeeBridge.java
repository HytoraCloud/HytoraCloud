package de.lystx.hytoracloud.bridge.proxy.bungeecord;

import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.player.PlayerInjectListener;
import de.lystx.hytoracloud.driver.service.bridge.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.bridge.proxy.ProxyBridge;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.other.ProxyPingListener;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.player.CommandListener;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.player.PlayerListener;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.server.ServerKickListener;
import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.player.featured.IPlayerSettings;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.service.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.service.minecraft.chat.CloudComponentAction;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.utils.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;


import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.wrapped.PlayerSettingsObject;
import de.lystx.hytoracloud.driver.utils.other.Action;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Consumer;

@Getter
public class BungeeBridge extends Plugin implements BridgeInstance {

    @Getter
    private static BungeeBridge instance;

    private Action action;

    @Override
    public void onEnable() {
        CloudBridge.load(this);

        CloudDriver.getInstance().getExecutorService().execute(() -> {
            instance = this;

            this.action = new Action();

            ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPingListener());


            CloudBridge.getInstance().setProxyBridge(new ProxyBridge() {

                @Override
                public IPlayerSettings getSettings(UUID uniqueId) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);

                    if (player == null) {
                        return null;
                    }

                    return new PlayerSettingsObject(
                            player.getLocale(),
                            player.hasChatColors(),
                            player.getViewDistance(),
                            player.getSkinParts().hasHat(),
                            player.getSkinParts().hasJacket(),
                            player.getSkinParts().hasRightSleeve(),
                            player.getSkinParts().hasLeftSleeve(),
                            player.getSkinParts().hasRightPants(),
                            player.getSkinParts().hasLeftPants(),
                            player.getSkinParts().hasCape(),
                            IPlayerSettings.ChatMode.valueOf(player.getChatMode().name()),
                            IPlayerSettings.MainHand.valueOf(player.getMainHand().name())
                    );
                }

                @Override
                public int getPing(UUID uniqueId) {

                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                    if (player == null) {
                        return -1;
                    }
                    return player.getPing();
                }

                @Override
                public void kickPlayer(UUID uniqueId, String reason) {

                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                    if (player == null) {
                        return;
                    }
                    player.disconnect(reason);
                }

                @Override
                public void connectPlayer(UUID uniqueId, String server) {

                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                    if (player == null) {
                        return;
                    }
                    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);

                    if (serverInfo != null) {
                        player.connect(serverInfo);
                    }
                }

                @Override
                public void fallbackPlayer(UUID uniqueId) {

                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                    if (player == null) {
                        return;
                    }

                    ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUniqueId());

                    if (cloudPlayer == null) {
                        return;
                    }
                    IService fallback = CloudDriver.getInstance().getFallbackManager().getFallback(cloudPlayer);

                    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(fallback.getName());

                    if (serverInfo == null) {
                        return;
                    }

                    player.connect(serverInfo);
                }

                @Override
                public void messagePlayer(UUID uniqueId, String message) {

                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                    if (player == null) {
                        return;
                    }
                    player.sendMessage(message);
                }

                @Override
                public void sendComponent(UUID uniqueId, ChatComponent chatComponent) {

                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                    if (player == null) {
                        return;
                    }
                    player.sendMessage(fromCloud(chatComponent));
                }

                @Override
                public void stopServer(IService service) {
                    if (service == null || service.getName() == null) {
                        return;
                    }
                    this.removeServer(service.getName());
                }

                @Override
                public List<String> getAllServices() {
                    List<String> list = new ArrayList<>();
                    for (ServerInfo serverInfo : new LinkedList<>(ProxyServer.getInstance().getServers().values())) {
                        list.add(serverInfo.getName());
                    }
                    return list;
                }

                @Override
                public Map<String, UUID> getPlayerInfos() {
                    Map<String, UUID> map = new HashMap<>();
                    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                        map.put(player.getName(), player.getUniqueId());
                    }
                    return map;
                }

                @Override
                public void registerService(IService service) {
                    if (service == null || service.getName() == null) {
                        return;
                    }
                    if (ProxyServer.getInstance().getServerInfo(service.getName()) == null) {
                        System.out.println("[" + service.getName() + "] <-> ServerRegister [" + (CloudDriver.getInstance().getServiceManager().getThisService() == null ? "GlobalProxy" : CloudDriver.getInstance().getServiceManager().getThisService().getName()) + "] has connected");
                    }
                    ServerInfo info = ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress(service.getHost(), service.getPort()), "CloudService", false);
                    ProxyServer.getInstance().getServers().put(service.getName(), info);
                }

                @Override
                public void removeServer(String server) {
                    if (server == null) {
                        return;
                    }
                    if (ProxyServer.getInstance().getServerInfo(server) != null) {
                        System.out.println("[" + server + "] <-> ServerRegister [" + CloudDriver.getInstance().getServiceManager().getThisService().getName() + "] has disconnected");
                    }
                    ProxyServer.getInstance().getServers().remove(server);
                }

                @Override
                public void stopProxy() {
                    ProxyServer.getInstance().stop();
                }

                @Override
                public ProxyVersion getVersion() {
                    return ProxyVersion.BUNGEECORD;
                }

            });

            this.bootstrap();
        });
    }


    /**
     * Creates a {@link TextComponent} from a {@link ChatComponent}
     *
     * @param chatComponent the cloudComponent
     * @return built md5 textComponent
     */
    private TextComponent fromCloud(ChatComponent chatComponent) {
        TextComponent textComponent = new TextComponent(chatComponent.getMessage());
        chatComponent.getActions().forEach((action1, objects) -> {

            if (action1 != null && objects != null) {
                if (action1.equals(CloudComponentAction.CLICK_EVENT_RUN_COMMAND)) {
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, (String) objects[0]));
                } else if (action1.equals(CloudComponentAction.CLICK_EVENT_OPEN_URL)) {
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, (String) objects[0]));
                } else if (action1.equals(CloudComponentAction.CLICK_EVENT_SUGGEST_COMMAND)) {
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, (String) objects[0]));
                } else if (action1.equals(CloudComponentAction.HOVER_EVENT_SHOW_TEXT)) {
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("" + objects[0])}));
                } else if (action1.equals(CloudComponentAction.HOVER_EVENT_SHOW_ENTITY)) {

                } else {

                }

            }
        });
        chatComponent.getChatComponents().forEach(component -> textComponent.addExtra(this.fromCloud(component)));
        return textComponent;
    }

    @Override
    public void onDisable() {
        this.shutdown();
    }

    public void bootstrap() {

        //Registers all Listeners
        this.getProxy().getPluginManager().registerListener(this, new CommandListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerKickListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerInjectListener());

        CloudDriver.getInstance().getMessageManager().registerChannel("smart-proxy", new Consumer<IChannelMessage>() {
            @SneakyThrows
            @Override
            public void accept(IChannelMessage channelMessage) {
                if (channelMessage.getKey().equalsIgnoreCase("PROXY_SET_IP")) {
                    JsonDocument document = channelMessage.getDocument();
                    InetSocketAddress client_address = Utils.getAddress(document.getString("CLIENT_ADDRESS"));
                    InetSocketAddress channel_address = Utils.getAddress(document.getString("CHANNEL_ADDRESS"));
                    CloudBridge.getInstance().getAddresses().put(channel_address, client_address);
                }
            }
        });

        if (CloudDriver.getInstance().getConfigManager().getProxyConfig() == null) {
            CloudDriver.getInstance().messageCloud(CloudDriver.getInstance().getServiceManager().getThisService().getName(), "§cCouldn't find §eProxyConfig §cfor this service!");
            System.out.println("[CloudBridge] Couldn't find ProxyConfig!");
        }
        System.out.println("[CloudBridge] Booted up in " + this.action.time() + "ms");
    }

    @Override
    public void flushCommand(String command) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
    }

    @Override
    public int getPing(UUID playerUniqueId) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUniqueId);
        if (player == null) {
            return -1;
        }
        return player.getPing();
    }

    @Override
    public PropertyObject requestProperties() {

        IService service = CloudDriver.getInstance().getServiceManager().getThisService();
        PropertyObject propertyObject = new PropertyObject();

        propertyObject.append("bungeeCord",
                new PropertyObject()
                    .append("name", ProxyServer.getInstance().getName())
                    .append("version", ProxyServer.getInstance().getVersion())
                    .append("protocolVersion", ProxyServer.getInstance().getProtocolVersion())
                    .append("gameVersion", ProxyServer.getInstance().getGameVersion())
                    .append("channels", ProxyServer.getInstance().getChannels())
        );

        propertyObject.append("service",
                new PropertyObject()
                        .append("max-players", service.getGroup().getMaxPlayers())
                        .append("players", ProxyServer.getInstance().getOnlineCount())
                        .append("online-mode", CloudDriver.getInstance().getConfigManager().getProxyConfig().isOnlineMode())
        );

        List<JsonObject<?>> plugins = new LinkedList<>();
        for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
            PluginDescription desc = plugin.getDescription();

            plugins.add(
                    JsonObject.serializable()
                        .append("name", desc.getName())
                        .append("website", "None")
                        .append("commands", new LinkedList<>())
                        .append("description", desc.getDescription())
                        .append("version", desc.getVersion())
                        .append("authors", Collections.singletonList(desc.getAuthor()))
                        .append("dependencies", desc.getDepends())
                        .append("soft-dependencies", desc.getSoftDepends())
                        .append("main-class", desc.getMain())
            );
        }

        propertyObject.append("plugins", plugins);

        return propertyObject;
    }

    @Override
    public void sendTabList(UUID uniqueId, String header, String footer) {

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
        if (player == null) {
            return;
        }

        player.setTabHeader(
                new TextComponent(header),
                new TextComponent(footer)
        );
    }

    @Override
    public void sendMessage(UUID uniqueId, ChatComponent message) {

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
        if (player == null) {
            return;
        }
        player.sendMessage(fromCloud(message));
    }

    public void shutdown() {
        try {
            CloudDriver.getInstance().getConnection().shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServerEnvironment type() {
        return ServerEnvironment.PROXY;
    }

}
