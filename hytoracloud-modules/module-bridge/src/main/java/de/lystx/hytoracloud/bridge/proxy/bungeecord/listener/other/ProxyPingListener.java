package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.other;


import java.util.UUID;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.utils.enums.versions.MinecraftProtocol;
import de.lystx.hytoracloud.driver.event.events.network.DriverEventNetworkPing;
import de.lystx.hytoracloud.driver.utils.interfaces.PlaceHolder;
import de.lystx.hytoracloud.driver.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.wrapped.PlayerConnectionObject;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@Getter
public class ProxyPingListener implements Listener {


    private int nullPointers;

    public ProxyPingListener() {
        this.nullPointers = 0;
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {

        ServerPing ping = event.getResponse();
        try {
            int port = CloudDriver.getInstance().getServiceManager().getThisService().getPort();


            NetworkConfig networkConfig = CloudDriver.getInstance().getConfigManager().getNetworkConfig();

            ProxyConfig proxyConfig = CloudDriver.getInstance().getConfigManager().getProxyConfig();

            if (!proxyConfig.isEnabled()) {
                return;
            }
            Motd motd = CloudBridge.getInstance().loadRandomMotd();
            if (!motd.isEnabled()) {
                return;
            }
            UUID uniqueId = CloudDriver.getInstance().getPermissionPool().getUniqueIdFromIpAddress(event.getConnection().getAddress());
            if (uniqueId != null) {
                PlayerConnectionObject connectionObject = new PlayerConnectionObject(
                        uniqueId,
                        CloudDriver.getInstance().getPermissionPool().getNameByUUID(uniqueId),
                        event.getConnection().getAddress().getAddress().getHostAddress(),
                        event.getConnection().getAddress().getPort(),
                        MinecraftProtocol.valueOf(event.getConnection().getVersion()),
                        event.getConnection().isOnlineMode(),
                        event.getConnection().isLegacy()
                );

                CloudDriver.getInstance().getEventManager().callEvent(new DriverEventNetworkPing(connectionObject));

            }
            if (motd.getVersionString() != null && !motd.getVersionString().trim().isEmpty()) {
                ping.setVersion(new ServerPing.Protocol("§7" + ChatColor.translateAlternateColorCodes('&', PlaceHolder.apply(motd.getVersionString(), motd)), 2));
            }



            if (motd.getPlayerInfo().length != 0) {
                ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[motd.getPlayerInfo().length];
                for (int i = 0; i < motd.getPlayerInfo().length; i++) {
                    playerInfos[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', PlaceHolder.apply(motd.getPlayerInfo()[i])), UUID.randomUUID());
                }
                ping.setPlayers(new ServerPing.Players(networkConfig.getMaxPlayers(), CloudDriver.getInstance().getPlayerManager().getCachedObjects().size(), playerInfos));
            }
            ping.getPlayers().setMax(networkConfig.getMaxPlayers());

            ping.setDescription(ChatColor.translateAlternateColorCodes('&', PlaceHolder.apply(motd.getFirstLine(), motd)) + "\n" + ChatColor.translateAlternateColorCodes('&', PlaceHolder.apply(motd.getSecondLine(), motd)));

        } catch (Exception e) {
            ping.getPlayers().setMax(200);


        }
        event.setResponse(ping);

    }

}
