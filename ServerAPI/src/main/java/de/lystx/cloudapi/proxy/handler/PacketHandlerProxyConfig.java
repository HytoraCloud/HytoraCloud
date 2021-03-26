package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.network.ProxyServerPacketReceiveEvent;
import de.lystx.cloudsystem.library.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.List;

import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@AllArgsConstructor
public class PacketHandlerProxyConfig extends PacketHandlerAdapter {


    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerPacketReceiveEvent(packet));
        if (packet instanceof PacketOutGlobalInfo) {

            boolean mc = this.cloudAPI.getNetworkConfig().getNetworkConfig().isMaintenance();
            PacketOutGlobalInfo info = (PacketOutGlobalInfo)packet;

            cloudAPI.setNetworkConfig(info.getNetworkConfig());

            if (mc != info.getNetworkConfig().getNetworkConfig().isMaintenance()) {
                this.switchMaintenance(info.getNetworkConfig().getNetworkConfig().isMaintenance());
            }

            for (List<Service> value : info.getServices().values()) {
                for (Service service : value) {
                    if (ProxyServer.getInstance().getServerInfo(service.getName()) == null) {
                        ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress(service.getHost(), service.getPort()), "CloudService", false);
                        ProxyServer.getInstance().getServers().put(service.getName(), serverInfo);
                    }
                }
            }
            for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                Service service = CloudAPI.getInstance().getNetwork().getService(serverInfo.getName());
                if (service == null) {
                    ProxyServer.getInstance().getServers().remove(serverInfo.getName());
                }
            }
        }
    }


    public void switchMaintenance(Boolean to) {
        if (!to) {
            return;
        }
        CloudAPI.getInstance().getCloudPlayers().forEach(cloudPlayer -> {
            if (!CloudAPI.getInstance().getNetworkConfig().getNetworkConfig().getWhitelistedPlayers().contains(cloudPlayer.getName()) && !cloudPlayer.hasPermission("cloudsystem.network.maintenance")) {
                cloudPlayer.createConnection().disconnect(
                        CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getMaintenanceKickMessage().replace("%prefix%", CloudAPI.getInstance().getPrefix()));
            }
        });
    }
}
