package de.lystx.cloudsystem.library.service.network.connection.client.connection;

import de.lystx.cloudsystem.library.service.network.connection.adapter.AdapterHandler;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.Channel;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.client.backend.BackhandedServer;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

public class ConnectionServer extends BackhandedServer {

    public ConnectionServer(final AdapterHandler adapterHandler, final NetworkChannel networkChannel, int port) {
        super(port);
        registerMethod(networkChannel.getChannelID(), (pack, socket) -> {
            Packet packet = (Packet)pack.get(1);
            adapterHandler.handelAdapterHandler(networkChannel, packet);
        });
    }

    public void preStart() {
        registerLoginMethod();
    }

    public void sendPacket(NetworkChannel networkChannel, Packet packet) {
        broadcastMessage(new Channel(networkChannel.getChannelID(), packet));
    }
}
