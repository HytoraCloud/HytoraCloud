package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

public interface CloudExecutor  {

    /**
     * Interface method for sending packet
     * @param packet
     */
    void sendPacket(Packet packet);

    /**
     * Interface method for registering handler
     * @param adapter
     */
    void registerPacketHandler(Object adapter);

}
