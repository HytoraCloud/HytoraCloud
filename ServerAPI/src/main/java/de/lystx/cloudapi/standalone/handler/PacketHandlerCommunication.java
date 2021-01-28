package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunication;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerServerChange;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutExecuteCommand;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerCommunication extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunication) {
            PacketCommunication packetCommunication = (PacketCommunication)packet;
            packetCommunication.setSendBack(false);
            this.cloudAPI.sendPacket(packetCommunication);
        } else if (packet instanceof PacketPlayOutCloudPlayerServerChange) {
            PacketPlayOutCloudPlayerServerChange packetPlayOutCloudPlayerServerChange = (PacketPlayOutCloudPlayerServerChange)packet;
            CloudPlayer cloudPlayer = packetPlayOutCloudPlayerServerChange.getCloudPlayer();
            cloudPlayer.setServer(packetPlayOutCloudPlayerServerChange.getNewServer());
            CloudAPI.getInstance().getCloudPlayers().update(cloudPlayer.getName(), cloudPlayer);
        }
    }
}
