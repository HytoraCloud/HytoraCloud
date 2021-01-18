package de.lystx.cloudapi.proxy;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.command.ListCommand;
import de.lystx.cloudapi.proxy.command.PermsCommand;
import de.lystx.cloudapi.proxy.handler.*;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInRegister;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudapi.proxy.command.CloudCommand;
import de.lystx.cloudapi.proxy.command.HubCommand;
import de.lystx.cloudapi.proxy.listener.*;
import de.lystx.cloudapi.proxy.manager.HubManager;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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

        this.getProxy().getPluginManager().registerListener(this, new ProxyPingListener());
        this.getProxy().getPluginManager().registerListener(this, new TablistListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerKickListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerConnectListener());

        this.getProxy().getPluginManager().registerCommand(this, new PermsCommand());
        this.getProxy().getPluginManager().registerCommand(this, new CloudCommand());
        this.getProxy().getPluginManager().registerCommand(this, new ListCommand());

        this.cloudAPI.sendPacket(new PacketPlayInRegister(this.cloudAPI.getService()));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.cloudAPI.shutdown()));
    }

    @Override
    public void onDisable() {
    }


    public int getProxyPort() {
        for (ListenerInfo listener : ProxyServer.getInstance().getConfig().getListeners()) {
            return listener.getHost().getPort();
        }
        return -1;
    }

    public boolean updatePermissions(ProxiedPlayer player) {
        try {
            CloudPlayerData data = this.cloudAPI.getPermissionPool().getPlayerDataOrDefault(player.getName());
            if (data.isDefault() || !this.cloudAPI.getPermissionPool().isRankValid(player.getName())) {
                data.setUuid(player.getUniqueId());
                try {
                    data.setIpAddress(player.getAddress().getHostName());
                } catch (NullPointerException e) {
                    data.setIpAddress("0");
                }
                this.cloudAPI.getPermissionPool().updatePlayerData(player.getName(), data);
                this.cloudAPI.getPermissionPool().update(this.cloudAPI.getCloudClient());
            }
            PermissionGroup group = this.cloudAPI.getPermissionPool().getPermissionGroupFromName(data.getPermissionGroup());
            if (group == null) {
                this.cloudAPI.messageCloud("ProxyCloudAPI", "§cTried updating permissions for §e" + player.getName() + " §cbut his permissionGroup wasn't found!");
                return false;
            }
            for (String permission : group.getPermissions()) {
                player.setPermission(permission, true);
            }
            for (String i : group.getInheritances()) {
                PermissionGroup inheritance = this.cloudAPI.getPermissionPool().getPermissionGroupFromName(i);
                for (String permission : inheritance.getPermissions()) {
                    player.setPermission(permission, true);
                }
            }
            for (String permission : data.getPermissions()) {
                player.setPermission(permission, true);
            }

        } catch (Exception e) {
            this.cloudAPI.messageCloud("ProxyCloudAPI", "§cCouldnt update permissions of §e" + player.getName() + "§c!");
            this.cloudAPI.messageCloud("ProxyCloudAPI", "§cException: §e" + e.getMessage());
            return false;
        }
        return true;
    }
}
