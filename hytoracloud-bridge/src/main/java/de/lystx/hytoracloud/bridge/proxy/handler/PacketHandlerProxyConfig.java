package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.proxy.events.network.ProxyServerPacketReceiveEvent;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.handler.PacketHandler;

import io.thunder.packet.Packet;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PacketHandlerProxyConfig implements PacketHandler {

    public void handle(Packet packet) {
        ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerPacketReceiveEvent(packet));
        if (packet instanceof PacketOutGlobalInfo) {
            this.handleGlobalInfo((PacketOutGlobalInfo) packet);
        }
    }

    
    public void handleGlobalInfo(PacketOutGlobalInfo info) {

        CloudDriver.getInstance().getImplementedData().put("networkConfig", info.getNetworkConfig());

        if (info.getNetworkConfig().getNetworkConfig().isMaintenance()) {
            this.switchMaintenance(info.getNetworkConfig().getNetworkConfig().isMaintenance());
        }

        for (List<Service> value : info.getServices().values()) {
            for (Service service : value) {
                if (ProxyServer.getInstance().getServerInfo(service.getName()) == null) {
                    ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress(CloudDriver.getInstance().getHost().getAddress().getHostAddress(), service.getPort()), "CloudService", false);
                    ProxyServer.getInstance().getServers().put(service.getName(), serverInfo);
                }
            }
        }
        for (ServerInfo serverInfo : new LinkedList<>(ProxyServer.getInstance().getServers().values())) {
            Service service = CloudDriver.getInstance().getServiceManager().getService(serverInfo.getName());
            if (service == null) {
                ProxyServer.getInstance().getServers().remove(serverInfo.getName());
            }
        }
    }

    /**
     * Changes Network Maintenance
     * @param to
     */
    public void switchMaintenance(Boolean to) {
        if (!to) {
            return;
        }
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!CloudDriver
                    .getInstance()
                    .getNetworkConfig()
                    .getNetworkConfig()
                    .getWhitelistedPlayers()
                    .contains(player.getName())
                    && !player.hasPermission("cloudsystem.network.maintenance")) {
                player.disconnect(
                        CloudDriver.getInstance()
                                .getNetworkConfig()
                                .getMessageConfig()
                                .getMaintenanceKickMessage()
                                .replace("%prefix%",
                                        CloudDriver.getInstance().getCloudPrefix()));
            }
        }
    }
}
