package de.lystx.cloudsystem.library.elements.packets.out.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class PacketPlayOutCloudPlayerServerChange extends Packet {

    private final CloudPlayer cloudPlayer;
    private final String newServer;

    public PacketPlayOutCloudPlayerServerChange(CloudPlayer cloudPlayer, String newServer) {
        this.cloudPlayer = cloudPlayer;
        this.newServer = newServer;
    }
}
