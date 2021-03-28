package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.list.Filter;
import de.lystx.cloudsystem.library.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Constants;
import de.lystx.cloudsystem.library.service.util.Serializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;

@Getter @AllArgsConstructor
public class PacketHandlerConfig extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override @SneakyThrows
    public void handle(Packet packet) {
        if (packet instanceof PacketOutGlobalInfo) {
            this.cloudAPI.setJoinable(true);
            PacketOutGlobalInfo info = (PacketOutGlobalInfo)packet;
            this.cloudAPI.getCloudPlayers().getAll().clear();
            this.cloudAPI.setNetworkConfig(info.getNetworkConfig());
            this.cloudAPI.getNetwork().setServices(info.getServices());
            this.cloudAPI.getCloudPlayers().setCloudPlayers(info.getCloudPlayers());

            Constants.CLOUDPLAYERS = new Filter<>(this.cloudAPI.getCloudPlayers().getAll());
            Constants.PERMISSION_POOL = this.cloudAPI.getPermissionPool();
            Constants.SERVICE_FILTER = new Filter<>(this.cloudAPI.getNetwork().getServices());

        }
    }
}
