package de.lystx.cloudsystem.library.elements.packets.wrapper;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

@Getter
public class WrapperPacketLoginRequest extends Packet {

    private final String name;
    private final String key;

    public WrapperPacketLoginRequest(String name, String key) {
        this.name = name;
        this.key = key;
    }
}
