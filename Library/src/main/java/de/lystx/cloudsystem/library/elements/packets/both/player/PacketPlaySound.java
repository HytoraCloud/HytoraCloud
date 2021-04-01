package de.lystx.cloudsystem.library.elements.packets.both.player;


import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter  @AllArgsConstructor
public class PacketPlaySound extends PacketCommunication {

    private final String name;
    private final String sound;
    private final float v1;
    private final float v2;

}
