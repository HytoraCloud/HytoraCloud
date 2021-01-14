package de.lystx.cloudsystem.library.elements.packets.out.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class PacketPlayOutCloudPlayers extends Packet implements Serializable {

    private final List<CloudPlayer> cloudPlayers;

    public PacketPlayOutCloudPlayers(List<CloudPlayer> cloudPlayers) {
        super(PacketPlayOutCloudPlayers.class);
        this.cloudPlayers = cloudPlayers;
    }
}
