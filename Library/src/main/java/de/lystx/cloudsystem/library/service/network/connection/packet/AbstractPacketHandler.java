package de.lystx.cloudsystem.library.service.network.connection.packet;

import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.client.connection.ConnectionClient;
import de.lystx.cloudsystem.library.service.network.connection.client.connection.ConnectionServer;

public interface AbstractPacketHandler {

    void registerPacket(Byte paramByte, Class<? extends Packet> paramClass);

    void unregisterPacket(Byte paramByte);

    void sendPacket(NetworkChannel paramNetworkChannel, ConnectionServer paramConnectionServer, Packet paramPacket);

    void sendPacket(NetworkChannel paramNetworkChannel, ConnectionClient paramConnectionClient, Packet paramPacket);
}
