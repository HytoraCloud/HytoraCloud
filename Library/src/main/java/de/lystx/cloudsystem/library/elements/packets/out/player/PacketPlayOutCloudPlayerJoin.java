package de.lystx.cloudsystem.library.elements.packets.out.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class PacketPlayOutCloudPlayerJoin extends Packet {

    private final CloudPlayer cloudPlayer;

    public PacketPlayOutCloudPlayerJoin(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }
}
