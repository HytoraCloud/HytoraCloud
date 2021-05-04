package de.lystx.cloudsystem.library.network.extra.util;

import de.lystx.cloudsystem.library.network.packet.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.cloudsystem.library.network.packet.AbstractPacket;

@AllArgsConstructor @Getter
public enum Protocol {

    HANDSHAKE(PacketHandshake.class),
    REQUEST(PacketRequest.class),
    RESPOND(PacketRespond.class);

    public static final int VERSION = values().length + 42;

    @Getter
    private final Class<? extends AbstractPacket> packetClass;

}
