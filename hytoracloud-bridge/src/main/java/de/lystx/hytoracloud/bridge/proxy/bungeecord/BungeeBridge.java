package de.lystx.hytoracloud.bridge.proxy.bungeecord;

import de.lystx.hytoracloud.driver.commons.interfaces.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.commons.interfaces.ProxyBridge;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.cloud.CloudListener;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.other.ProxyPingListener;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.other.TablistListener;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.player.CommandListener;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.player.PlayerListener;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.server.ServerConnectListener;
import de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.server.ServerKickListener;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.CloudComponent;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.CloudComponentAction;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;



import utillity.Action;
import de.lystx.hytoracloud.driver.CloudDriver;
import utillity.PropertyObject;
import lombok.Getter;
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

@Getter
public class BungeeBridge extends Plugin implements BridgeInstance {

    @Getter
    private static BungeeBridge instance;

    private Action action;

    @Override
    public void onEnable() {
        CloudBridge.load(this);

        CloudDriver.getInstance().execute(() -> {
            instance = this;

            this.action = new Action();

            ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPingListener());
            ProxyServer.getInstance().getPluginManager().registerListener(this, new TablistListener());

            CloudBridge.getInstance().setProxyBridge(new ProxyBridge() {

                @Override
                public void updateTabList() {
                    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {

                        TabList tabList = CloudBridge.getInstance().loadRandomTablist();

                        if (!CloudDriver.getInstance().getProxyConfig().isEnabled() || !tabList.isEnabled()) {
                            return;
                        }

                        ICloudPlayer cachedPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUniqueId());

                        player.setTabHeader(
                                new TextComponent(formatTabList(cachedPlayer, tabList.getHeader())),
                                new TextComponent(formatTabList(cachedPlayer, tabList.getFooter())
                                )
                        );
                    }
                }

                @Override
                public NetworkHandler getNetworkHandler() {
                    return new CloudListener();
                }

                @Override
                public long getPing(UUID uniqueId) {

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

                    ICloudPlayer cloudPlayer = ICloudPlayer.fromUUID(player.getUniqueId());

                    if (cloudPlayer == null) {
                        return;
                    }
                    IService fallback = CloudDriver.getInstance().getFallback(cloudPlayer);

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
                public void sendComponent(UUID uniqueId, CloudComponent cloudComponent) {

                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
                    if (player == null) {
                        return;
                    }
                    player.sendMessage(fromCloud(cloudComponent));
                }

                @Override
                public void stopServer(IService service) {
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
                    if (ProxyServer.getInstance().getServerInfo(service.getName()) == null) {
                        System.out.println("[" + service.getName() + "] <-> ServerRegister [" + (CloudDriver.getInstance().getCurrentService() == null ? "GlobalProxy" : CloudDriver.getInstance().getCurrentService().getName()) + "] has connected");
                    }
                    ServerInfo info = ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress(service.getHost(), service.getPort()), "CloudService", false);
                    ProxyServer.getInstance().getServers().put(service.getName(), info);
                }

                @Override
                public void removeServer(String server) {
                    if (ProxyServer.getInstance().getServerInfo(server) != null) {
                        System.out.println("[" + server + "] <-> ServerRegister [" + CloudDriver.getInstance().getCurrentService().getName() + "] has disconnected");
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
     * Creates a {@link TextComponent} from a {@link CloudComponent}
     *
     * @param cloudComponent the cloudComponent
     * @return built md5 textComponent
     */
    private TextComponent fromCloud(CloudComponent cloudComponent) {
        TextComponent textComponent = new TextComponent(cloudComponent.getMessage());
        cloudComponent.getActions().forEach((action1, objects) -> {

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
        cloudComponent.getCloudComponents().forEach(component -> textComponent.addExtra(this.fromCloud(component)));
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
        this.getProxy().getPluginManager().registerListener(this, new ServerConnectListener());


        if (CloudDriver.getInstance().getProxyConfig() == null) {
            CloudDriver.getInstance().messageCloud(CloudDriver.getInstance().getCurrentService().getName(), "§cCouldn't find §eProxyConfig §cfor this service!");
            System.out.println("[CloudBridge] Couldn't find ProxyConfig!");
        }
        System.out.println("[CloudBridge] Booted up in " + this.action.time() + "ms");

    }

    @Override
    public void flushCommand(String command) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
    }

    @Override
    public PropertyObject requestProperties() {

        IService service = CloudDriver.getInstance().getCurrentService();
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
                        .append("online-mode", CloudDriver.getInstance().getProxyConfig().isOnlineMode())
        );

        List<PropertyObject> plugins = new LinkedList<>();
        for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
            PluginDescription desc = plugin.getDescription();

            plugins.add(
                    new PropertyObject()
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

    public void shutdown() {
        try {
            CloudDriver.getInstance().getConnection().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
