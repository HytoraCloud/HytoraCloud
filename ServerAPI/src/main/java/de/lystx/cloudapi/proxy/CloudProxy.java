package de.lystx.cloudapi.proxy;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.command.*;
import de.lystx.cloudapi.proxy.handler.*;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudapi.proxy.listener.*;
import de.lystx.cloudapi.proxy.manager.HubManager;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

@Getter
public class CloudProxy extends Plugin {

    @Getter
    private static CloudProxy instance;

    private CloudAPI cloudAPI;
    private HubManager hubManager;
    private NetworkManager networkManager;
    private List<Service> services;

    @Override
    public void onEnable() {
        instance = this;

        this.cloudAPI = new CloudAPI();
        this.hubManager = new HubManager();
        this.networkManager = new NetworkManager();
        this.services = new LinkedList<>();

        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyStartServer(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyStopServer(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyConfig(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyCloudPlayerHandler(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyStop(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyChatEvent(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerProxyEvent(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerHandler(new CloudListener());

        this.getProxy().getPluginManager().registerListener(this, new ProxyPingListener());
        this.getProxy().getPluginManager().registerListener(this, new TablistListener());
        this.getProxy().getPluginManager().registerListener(this, new CommandListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerKickListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerConnectListener());

        this.cloudAPI.registerCommand(new CloudCommand());
        this.cloudAPI.registerCommand(new HubCommand());
        this.cloudAPI.registerCommand(new ListCommand());
        this.cloudAPI.registerCommand(new WhereAmICommand());
        this.cloudAPI.registerCommand(new WhereIsCommand());
        this.cloudAPI.registerCommand(new PermsCommand());
        this.cloudAPI.registerCommand(new NetworkCommand());

        if (this.getProxyConfig() == null) {
            this.cloudAPI.messageCloud(this.cloudAPI.getService().getName(), "§cCouldn't find §eProxyConfig §cfor this service!");
            System.out.println("[CloudAPI] Couldn't find ProxyConfig!");
        }

    }

    @Override
    public void onDisable() {
        if (this.cloudAPI.getCloudClient().isConnected()) {
            this.cloudAPI.disconnect();
        }
    }

    public void executeCommand(String line) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), line);
    }

    public ProxyConfig getProxyConfig() {
        return this.cloudAPI.getService().getServiceGroup().getValues().get("proxyConfig", ProxyConfig.class);
    }

    public int getProxyPort() {
        for (ListenerInfo listener : ProxyServer.getInstance().getConfig().getListeners()) {
            return listener.getHost().getPort();
        }
        return -1;
    }

}
