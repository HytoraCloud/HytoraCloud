package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class PacketPlayInCloudPlayerServerChange extends Packet implements Serializable {

    private final UUID uuid;
    private final String newServer;

    public PacketPlayInCloudPlayerServerChange(UUID uuid, String newServer) {
        super(PacketPlayInCloudPlayerServerChange.class);
        this.uuid = uuid;
        this.newServer = newServer;
    }
}
