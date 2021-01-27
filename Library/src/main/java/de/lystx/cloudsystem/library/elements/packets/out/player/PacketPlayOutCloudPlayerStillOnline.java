package de.lystx.cloudsystem.library.elements.packets.out.player;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunication;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutCloudPlayerStillOnline extends PacketCommunication implements Serializable {

    private final String playerName;

    public PacketPlayOutCloudPlayerStillOnline(String playerName) {
        this.playerName = playerName;
    }
}
