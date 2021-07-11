package de.lystx.hytoracloud.bridge.bungeecord;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.bridge.bungeecord.listener.network.CloudListener;
import de.lystx.hytoracloud.bridge.bungeecord.listener.other.ProxyPingListener;
import de.lystx.hytoracloud.bridge.bungeecord.listener.other.TablistListener;
import de.lystx.hytoracloud.bridge.bungeecord.listener.player.CommandListener;
import de.lystx.hytoracloud.bridge.bungeecord.listener.player.PlayerListener;
import de.lystx.hytoracloud.bridge.bungeecord.listener.server.ServerConnectListener;
import de.lystx.hytoracloud.bridge.bungeecord.listener.server.ServerKickListener;
import de.lystx.hytoracloud.bridge.bungeecord.manager.HubManager;
import de.lystx.hytoracloud.driver.commons.chat.CloudComponent;
import de.lystx.hytoracloud.driver.commons.chat.CloudComponentAction;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerServerChange;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.service.global.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.service.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;



import de.lystx.hytoracloud.driver.utils.utillity.Action;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

@Getter
public class HytoraCloudBungeeCordBridge extends Plugin {

    @Getter
    private static HytoraCloudBungeeCordBridge instance;

    private HubManager hubManager;
    private Action action;

    @Override
    public void onEnable() {
        CloudBridge.load();

        CloudDriver.getInstance().execute(() -> {
            instance = this;

            this.action = new Action();
            this.hubManager = new HubManager();

            ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPingListener());
            ProxyServer.getInstance().getPluginManager().registerListener(this, new TablistListener());


            CloudBridge.getInstance().setProxyBridge(new ProxyBridge() {

                @Override
                public String formatTabList(CloudPlayer cloudPlayer, String input) {
                    try {
                        Service service;
                        PermissionGroup permissionGroup;
                        if (cloudPlayer == null || cloudPlayer.getPermissionGroup() == null) {
                            permissionGroup = new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new HashMap<>());
                        } else {
                            permissionGroup = cloudPlayer.getCachedPermissionGroup();
                        }
                        if (cloudPlayer == null || cloudPlayer.getService() == null) {
                            service = CloudDriver.getInstance().getThisService();
                        } else {
                            service = CloudDriver.getInstance().getServiceManager().getService(cloudPlayer.getService().getName());
                        }
                        return input
                                .replace("&", "§")
                                .replace("%max_players%", String.valueOf(CloudDriver.getInstance().getProxyConfig().getMaxPlayers()))
                                .replace("%online_players%", String.valueOf(CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size()))
                                .replace("%id%", service.getServiceID() + "")
                                .replace("%group%", service.getServiceGroup().getName() + "")
                                .replace("%rank%", permissionGroup == null ? "No group found" : permissionGroup.getName())
                                .replace("%receiver%", CloudDriver.getInstance().getThisService().getServiceGroup().getReceiver())
                                .replace("%rank_color%", permissionGroup == null ? "§7" : permissionGroup.getDisplay())
                                .replace("%proxy%", CloudDriver.getInstance().getServiceManager().getProxy(getProxyPort()).getName())
                                .replace("%server%", service.getName());

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public void updateTabList() {
                    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {

                        TabList tabList = CloudBridge.getInstance().loadRandomTablist();

                        if (!CloudDriver.getInstance().getProxyConfig().isEnabled() || !tabList.isEnabled()) {
                            return;
                        }

                        CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getUniqueId());

                        player.setTabHeader(
                                new TextComponent(formatTabList(cloudPlayer, tabList.getHeader())),
                                new TextComponent(formatTabList(cloudPlayer, tabList.getFooter())
                                )
                        );
                    }
                }

                @Override
                public boolean onServerKick(CloudPlayer cloudPlayer, Service kickedFromService) {
                    try {
                        Service fallback = CloudDriver.getInstance().getFallback(cloudPlayer);
                        cloudPlayer.connect(fallback);
                        return true;
                    } catch (NullPointerException e) {
                        return false;
                    }
                }

                @Override
                public void onServerConnect(CloudPlayer cloudPlayer, Service service) {
                    DriverEventPlayerServerChange serverChange = new DriverEventPlayerServerChange(cloudPlayer, service);

                    CloudDriver.getInstance().callEvent(serverChange);
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

                    HytoraCloudBungeeCordBridge.getInstance().getHubManager().sendPlayerToFallback(player);
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
                public void stopServer(Service service) {
                    ProxyServer.getInstance().getServers().remove(service.getName());
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
                public void registerService(Service service) {
                    if (ProxyServer.getInstance().getServerInfo(service.getName()) == null) {
                        ServerInfo info = ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress(CloudDriver.getInstance().getHost().getAddress().getHostName(), service.getPort()), "CloudService", false);
                        ProxyServer.getInstance().getServers().put(service.getName(), info);
                    }
                }

                @Override
                public void removeServer(String server) {
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

    }

    public void shutdown() {
        try {
            CloudDriver.getInstance().getConnection().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
