package de.lystx.cloudsystem.library.elements.packets.communication;


import lombok.Getter;

import java.util.UUID;

@Getter
public class PacketCommunicationPlaySound extends PacketCommunication {

    private final String name;
    private final String sound;
    private final float v1;
    private final float v2;

    public PacketCommunicationPlaySound(String name, String sound, float v1, float v2) {
        super(PacketCommunicationPlaySound.class);
        this.name = name;
        this.sound = sound;
        this.v1 = v1;
        this.v2 = v2;
    }
}
