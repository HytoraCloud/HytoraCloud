package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutNetworkConfig;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

@Getter
public class PacketHandlerConfig extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerConfig(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutNetworkConfig) {
            PacketPlayOutNetworkConfig packetPlayOutNetworkConfig = (PacketPlayOutNetworkConfig)packet;
            this.cloudAPI.setNetworkConfig(packetPlayOutNetworkConfig.getNetworkConfig());

        }
    }
}
