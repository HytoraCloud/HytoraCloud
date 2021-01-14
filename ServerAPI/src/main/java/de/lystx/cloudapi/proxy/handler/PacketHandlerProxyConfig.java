package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutNetworkConfig;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutServices;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudapi.proxy.CloudProxy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class PacketHandlerProxyConfig extends PacketHandlerAdapter {


    private final CloudAPI cloudAPI;

    public PacketHandlerProxyConfig(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutServices) {
            CloudAPI.getInstance().setJoinable(true);
            PacketPlayOutServices packetPlayOutServices = (PacketPlayOutServices)packet;
            Map<ServiceGroup, List<Service>> services = packetPlayOutServices.getServices();
            for (List<Service> value : services.values()) {
                for (Service service : value) {
                    CloudProxy.getInstance().getServices().add(service);
                    if (ProxyServer.getInstance().getServerInfo(service.getName()) == null) {
                        ServerInfo info = ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress("127.0.0.1", service.getPort()), "CloudService", false);
                        ProxyServer.getInstance().getServers().put(service.getName(), info);
                    }
                }
            }
            for (Service service : CloudProxy.getInstance().getServices()) {
                if (CloudAPI.getInstance().getNetwork().getService(service.getName()) == null) {
                    CloudProxy.getInstance().getServices().remove(service);
                    ProxyServer.getInstance().getServers().remove(service.getName());
                }
            }
        } else if (packet instanceof PacketPlayOutNetworkConfig) {
            cloudAPI.getScheduler().scheduleDelayedTask(() -> {
                CloudProxy.getInstance().getNetworkManager().switchMaintenance(this.cloudAPI.getNetworkConfig().getProxyConfig().isMaintenance());
            }, 2L);
        }
    }
}
