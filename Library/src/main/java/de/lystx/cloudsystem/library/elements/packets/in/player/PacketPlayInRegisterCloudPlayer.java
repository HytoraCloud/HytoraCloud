package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInRegisterCloudPlayer extends Packet implements Serializable {

    private final CloudPlayer cloudPlayer;

    public PacketPlayInRegisterCloudPlayer(CloudPlayer cloudPlayer) {
        super();
        this.cloudPlayer = cloudPlayer;
    }
}
