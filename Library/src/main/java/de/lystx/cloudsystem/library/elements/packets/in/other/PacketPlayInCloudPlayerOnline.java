package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInCloudPlayerOnline extends Packet implements Serializable {

    private final String playerName;
    private final boolean online;

    public PacketPlayInCloudPlayerOnline(String playerName, boolean online) {
        this.playerName = playerName;
        this.online = online;
    }
}
