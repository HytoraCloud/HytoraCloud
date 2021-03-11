package de.lystx.cloudsystem.library.elements.packets.both;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketSendTitle extends PacketCommunication {

    private final String name;
    private final String title;
    private final String subtitle;

}
