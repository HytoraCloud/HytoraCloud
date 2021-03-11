package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.command.HubCommand;
import de.lystx.cloudapi.proxy.events.network.ProxyServerPacketReceiveEvent;
import de.lystx.cloudsystem.library.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudapi.proxy.CloudProxy;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
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
                CloudProxy.getInstance().getNetworkManager().switchMaintenance(info.getNetworkConfig().getNetworkConfig().isMaintenance());
            }
            if (info.getNetworkConfig().getNetworkConfig().isHubCommand()) {
                CloudAPI.getInstance().registerCommand(new HubCommand());
            } else {
                CloudAPI.getInstance().unregisterCommand(new HubCommand());
            }
            Map<ServiceGroup, List<Service>> services = info.getServices();
            for (List<Service> value : services.values()) {
                for (Service service : value) {
                    CloudProxy.getInstance().getServices().add(service);
                    if (ProxyServer.getInstance().getServerInfo(service.getName()) == null) {
                        ServerInfo i = ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress("127.0.0.1", service.getPort()), "CloudService", false);
                        ProxyServer.getInstance().getServers().put(service.getName(), i);
                    }
                }
            }
            for (Service service : CloudProxy.getInstance().getServices()) {
                if (CloudAPI.getInstance().getNetwork().getService(service.getName()) == null) {
                    CloudProxy.getInstance().getServices().remove(service);
                    ProxyServer.getInstance().getServers().remove(service.getName());
                }
            }
        }
    }
}
