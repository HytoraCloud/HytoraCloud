package de.lystx.cloudsystem.library.elements.packets.out.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class PacketPlayOutForceRegisterPlayer extends Packet implements Serializable {


    private final UUID uuid;

    public PacketPlayOutForceRegisterPlayer(UUID uuid) {
        super();
        this.uuid = uuid;
    }
}
