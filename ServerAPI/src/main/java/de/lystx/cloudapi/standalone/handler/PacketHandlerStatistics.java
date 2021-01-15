package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutStatistics;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

public class PacketHandlerStatistics extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerStatistics(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutStatistics) {
            PacketPlayOutStatistics packetPlayOutStatistics = (PacketPlayOutStatistics)packet;
            this.cloudAPI.setStatistics(packetPlayOutStatistics.getStatistics());
        }
    }
}
