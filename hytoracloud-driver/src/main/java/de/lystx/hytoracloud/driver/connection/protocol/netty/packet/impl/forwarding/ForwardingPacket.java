package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.forwarding;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;

public interface ForwardingPacket<V> extends IPacket {

    /**
     * Checks if it needs to be sent again
     *
     * @return boolean
     */
    boolean isForward();

    /**
     * Sets the forwarding state
     *
     * @param b boolean state
     * @return current packet
     */
    V forward(boolean b);
}
