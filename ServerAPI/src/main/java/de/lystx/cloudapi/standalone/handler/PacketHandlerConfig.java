package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.list.Filter;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketUpdateNetworkConfig;
import de.lystx.cloudsystem.library.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.util.CloudCache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import lombok.SneakyThrows;

@Getter @AllArgsConstructor
public class PacketHandlerConfig extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override @SneakyThrows
    public void handle(Packet packet) {
        if (packet instanceof PacketOutGlobalInfo) {
            this.cloudAPI.setJoinable(true);
            PacketOutGlobalInfo info = (PacketOutGlobalInfo)packet;
            this.cloudAPI.setNetworkConfig(info.getNetworkConfig());
            this.cloudAPI.getNetwork().setServices(info.getServices());
            this.cloudAPI.getCloudPlayers().setCloudPlayers(info.getCloudPlayers());

            CloudCache.getInstance().setCloudPlayerFilter(new Filter<>(info.getCloudPlayers()));
            CloudCache.getInstance().setPermissionPool(this.cloudAPI.getPermissionPool());
            CloudCache.getInstance().setServiceFilter(new Filter<>(this.cloudAPI.getNetwork().getServices()));

        }
    }

    @PacketHandler
    public void handle(PacketUpdateNetworkConfig packetUpdateNetworkConfig) {
        this.cloudAPI.setNetworkConfig(packetUpdateNetworkConfig.getNetworkConfig());
    }
}
