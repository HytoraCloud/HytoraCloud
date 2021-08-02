package de.lystx.hytoracloud.networking.elements.packet.handler;

import de.lystx.hytoracloud.networking.elements.packet.Packet;

public interface PacketHandler {

    /**
     * Called when a {@link Packet} is received
     * and needs to be handled
     *
     * @param packet the packet to handle
     */
    void handle(Packet packet);
}
