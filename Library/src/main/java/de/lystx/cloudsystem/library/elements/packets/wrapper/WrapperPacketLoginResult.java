package de.lystx.cloudsystem.library.elements.packets.wrapper;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

@Getter
public class WrapperPacketLoginResult extends Packet {

    private final String wrapperName;
    private final boolean allow;

    public WrapperPacketLoginResult(String wrapperName, boolean allow) {
        this.wrapperName = wrapperName;
        this.allow = allow;

    }
}
