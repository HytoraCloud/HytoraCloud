package de.lystx.cloudsystem.library.elements.packets.both;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketPlayerChat extends PacketCommunication{

    private final String player;
    private final String message;

}
