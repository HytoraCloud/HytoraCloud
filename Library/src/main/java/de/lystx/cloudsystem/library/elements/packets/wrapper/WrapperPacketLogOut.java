package de.lystx.cloudsystem.library.elements.packets.wrapper;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

@Getter
public class WrapperPacketLogOut extends Packet {

    private final String name;

    public WrapperPacketLogOut(String name) {
        this.name = name;
    }
}
