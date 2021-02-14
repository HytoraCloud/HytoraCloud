package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

public interface CloudExecutor  {

    void sendPacket(Packet packet);

    void registerPacketHandler(Object adapter);

}
