package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayers;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class PacketHandlerCloudPlayers extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerCloudPlayers(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutCloudPlayers) {
            PacketPlayOutCloudPlayers packetPlayOutCloudPlayers = (PacketPlayOutCloudPlayers)packet;
            this.cloudAPI.getCloudPlayers().setCloudPlayers(packetPlayOutCloudPlayers.getCloudPlayers());
        }
    }
}
