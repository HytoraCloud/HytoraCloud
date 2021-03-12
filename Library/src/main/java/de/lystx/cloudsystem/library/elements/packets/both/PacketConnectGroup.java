package de.lystx.cloudsystem.library.elements.packets.both;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketConnectGroup extends PacketCommunication implements Serializable {

    private final String name;
    private final String group;

}
