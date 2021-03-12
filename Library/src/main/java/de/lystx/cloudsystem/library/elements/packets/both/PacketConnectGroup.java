package de.lystx.cloudsystem.library.elements.packets.both;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketConnectGroup extends PacketCommunication {

    private final String name;
    private final String group;

}
