package de.lystx.cloudsystem.library.elements.packets.out.player;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunication;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class PacketPlayOutForceRegisterPlayer extends PacketCommunication implements Serializable {


    private final String uuid;

    public PacketPlayOutForceRegisterPlayer(String uuid) {
        super(PacketPlayOutForceRegisterPlayer.class);
        this.uuid = uuid;
    }
}
