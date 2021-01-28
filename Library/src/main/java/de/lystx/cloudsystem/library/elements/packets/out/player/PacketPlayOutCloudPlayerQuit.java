package de.lystx.cloudsystem.library.elements.packets.out.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class PacketPlayOutCloudPlayerQuit extends Packet {

    private final CloudPlayer cloudPlayer;

    public PacketPlayOutCloudPlayerQuit(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }
}
