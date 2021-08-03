package de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

public interface PacketHandler {

    /**
     * Called when a {@link Packet} is received
     * and needs to be handled
     *
     * @param packet the packet to handle
     */
    void handle(Packet packet);
}
