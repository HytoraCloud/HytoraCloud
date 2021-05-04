package de.lystx.cloudsystem.library.network.packet.response;

import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.impl.PacketRespond;
import lombok.Getter;

/**
 * This class defines the type of object which has to be returned from a query
 */
public enum ResponseScope {


    DEFAULT(AbstractPacket.class),
    RESPOND(PacketRespond.class),
    RESPONSE;

    /**
     * The class of the has-to-be-returned-packet (only if not {@link #RESPONSE})
     */
    @Getter
    private Class<? extends AbstractPacket> wrappedClass;

    ResponseScope(Class<? extends AbstractPacket> wrappedClass) {
        this.wrappedClass = wrappedClass;
    }

    ResponseScope() {
    }
}
