package net.hytora.networking.elements.packet.handler;

import net.hytora.networking.elements.packet.HytoraPacket;

public interface PacketHandler {

    /**
     * Called when a {@link HytoraPacket} is received
     * and needs to be handled
     *
     * @param packet the packet to handle
     */
    void handle(HytoraPacket packet);
}
