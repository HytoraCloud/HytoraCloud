package de.lystx.cloudsystem.library.service.network.connection.client.connection;

import de.lystx.cloudsystem.library.service.network.connection.adapter.AdapterHandler;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.Channel;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.client.backend.BackhandedClient;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

public class ConnectionClient extends BackhandedClient {

    public ConnectionClient(final AdapterHandler adapterHandler, final NetworkChannel networkChannel, String hostname, int port, int timeout) {
        super(hostname, port, timeout);
        registerMethod(networkChannel.getChannelID(), (pack, socket) -> {
            Packet packet = (Packet)pack.get(1);
            adapterHandler.handelAdapterHandler(networkChannel, packet);
        });
        start();
    }

    public void sendPacket(NetworkChannel networkChannel, Packet packet) {
        sendMessage(new Channel(networkChannel.getChannelID(), packet));
    }
}
