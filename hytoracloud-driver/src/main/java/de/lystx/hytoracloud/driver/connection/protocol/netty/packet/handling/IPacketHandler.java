package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;

public interface IPacketHandler {

    /**
     * Handles an incoming {@link IPacket}
     *
     * @param packet the packet
     */
    void handle(IPacket packet);
}
