package de.lystx.cloudsystem.library.service.network.connection.channel.handler;

import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.Provider;

import java.net.Socket;

public class NetworkChannelHandler extends AbstractNetworkChannelHandler {

    public void registerChannel(NetworkChannel networkChannel) {
        super.registerChannel(networkChannel);
    }

    public NetworkChannel getCloudChannel() {
        return super.getCloudChannel();
    }

    public void unregisterChannel(NetworkChannel networkChannel) {
        super.unregisterChannel(networkChannel);
    }

    public void bindSocketToChannel(Socket socket, Provider provider) {
        super.bindSocketToChannel(socket, provider);
    }

    public String toString() {
        return super.toString();
    }
}
