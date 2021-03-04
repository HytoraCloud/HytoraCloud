package de.lystx.cloudsystem.library.service.network.connection.adapter;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

public abstract class PacketHandlerAdapter {

    /**
     * Handles the incoming packet
     * @param packet
     */
    public abstract void handle(Packet packet);

}
