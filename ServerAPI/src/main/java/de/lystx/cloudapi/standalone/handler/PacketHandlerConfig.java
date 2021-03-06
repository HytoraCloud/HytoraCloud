package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutGlobalInfo;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@Getter @AllArgsConstructor
public class PacketHandlerConfig extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    /**
     * This packetHandler handles the {{@link PacketPlayOutGlobalInfo}} packet
     * to put all infos for the CloudAPI like players or Permissions or services etc
     * @param packet
     */
    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutGlobalInfo) {
            this.cloudAPI.setJoinable(true);
            PacketPlayOutGlobalInfo info = (PacketPlayOutGlobalInfo)packet;
            this.cloudAPI.getCloudPlayers().getAll().clear();
            this.cloudAPI.setNetworkConfig(info.getNetworkConfig());
            this.cloudAPI.setPermissionPool(info.getPermissionPool());
            this.cloudAPI.getNetwork().setServices(info.getServices());
            this.cloudAPI.getCloudPlayers().setCloudPlayers(info.getCloudPlayers());

            Constants.PERMISSION_POOL = this.cloudAPI.getPermissionPool();

            this.cloudAPI.getNetwork().getServices(ServiceType.PROXY).forEach(service -> {
                if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                    this.cloudAPI.getNetwork().getProxies().put(service.getPort(), service.getName());
                }
            });
        }
    }
}
