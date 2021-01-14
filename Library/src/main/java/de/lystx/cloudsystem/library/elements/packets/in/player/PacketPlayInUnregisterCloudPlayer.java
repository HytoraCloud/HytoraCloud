package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class PacketPlayInUnregisterCloudPlayer extends Packet implements Serializable {

    private final UUID uuid;

    public PacketPlayInUnregisterCloudPlayer(UUID uuid) {
        super(PacketPlayInUnregisterCloudPlayer.class);
        this.uuid = uuid;
    }
}
