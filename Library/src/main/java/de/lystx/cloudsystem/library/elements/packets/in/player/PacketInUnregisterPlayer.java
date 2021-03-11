package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.elements.packets.both.PacketCommunication;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInUnregisterPlayer extends PacketCommunication implements Serializable {

    private final String name;

    public PacketInUnregisterPlayer(String name) {
        this.setSendBack(true);
        this.name = name;
    }
}
