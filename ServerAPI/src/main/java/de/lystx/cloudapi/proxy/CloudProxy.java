package de.lystx.cloudapi.proxy;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.command.*;
import de.lystx.cloudapi.proxy.handler.*;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInRegister;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudapi.proxy.listener.*;
import de.lystx.cloudapi.proxy.manager.HubManager;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

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

        this.getProxy().getPluginManager().registerListener(this, new ProxyPingListener());
        this.getProxy().getPluginManager().registerListener(this, new TablistListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerKickListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerConnectListener());

        this.getProxy().getPluginManager().registerCommand(this, new PermsCommand());
        this.getProxy().getPluginManager().registerCommand(this, new CloudCommand());
        this.getProxy().getPluginManager().registerCommand(this, new ListCommand());
        this.getProxy().getPluginManager().registerCommand(this, new WhereIsCommand());
        this.getProxy().getPluginManager().registerCommand(this, new WhereAmICommand());


        this.cloudAPI.sendPacket(new PacketPlayInRegister(this.cloudAPI.getService()));
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

    public int getProxyPort() {
        for (ListenerInfo listener : ProxyServer.getInstance().getConfig().getListeners()) {
            return listener.getHost().getPort();
        }
        return -1;
    }

}
