package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutGlobalInfo;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutNetworkConfig;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import java.util.List;

@Getter
public class PacketHandlerConfig extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerConfig(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutGlobalInfo) {
            this.cloudAPI.setJoinable(true);
            PacketPlayOutGlobalInfo info = (PacketPlayOutGlobalInfo)packet;
            this.cloudAPI.getCloudPlayers().getAll().clear();
            this.cloudAPI.setNetworkConfig(info.getNetworkConfig());
            this.cloudAPI.setPermissionPool(info.getPermissionPool());
            this.cloudAPI.getNetwork().setServices(info.getServices());
            this.cloudAPI.setStatistics(info.getStatistics());
            this.cloudAPI.getCloudPlayers().setCloudPlayers(info.getCloudPlayers());

            for (List<Service> value : info.getServices().values()) {
                for (Service service : value) {
                    if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                        this.cloudAPI.getNetwork().getProxies().put(service.getPort(), service.getName());
                    }
                }
            }
        }
    }
}
