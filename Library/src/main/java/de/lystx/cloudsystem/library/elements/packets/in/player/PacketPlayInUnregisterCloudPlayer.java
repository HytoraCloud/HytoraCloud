package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunication;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInUnregisterCloudPlayer extends PacketCommunication implements Serializable {

    private final String name;

    public PacketPlayInUnregisterCloudPlayer(String name) {
        this.setSendBack(true);
        this.name = name;
    }
}
