package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInCloudPlayerServerChange extends Packet implements Serializable {

    private final CloudPlayer cloudPlayer;
    private final String newServer;

    public PacketPlayInCloudPlayerServerChange(CloudPlayer cloudPlayer, String newServer) {
        super();
        this.cloudPlayer = cloudPlayer;
        this.newServer = newServer;
    }
}
