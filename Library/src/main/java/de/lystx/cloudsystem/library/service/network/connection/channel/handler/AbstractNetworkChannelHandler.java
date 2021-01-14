package de.lystx.cloudsystem.library.service.network.connection.channel.handler;

import de.lystx.cloudsystem.library.service.network.connection.channel.base.Identifier;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.Provider;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNetworkChannelHandler {

    private final Map<Provider, NetworkChannel> networkChannelHashMap;

    public AbstractNetworkChannelHandler() {
        this.networkChannelHashMap = new HashMap<>();
    }

    public void registerChannel(NetworkChannel networkChannel) {
        this.networkChannelHashMap.put(networkChannel.getProvider(), networkChannel);
    }

    public void unregisterChannel(NetworkChannel networkChannel) {
        this.networkChannelHashMap.remove(networkChannel.getProvider(), networkChannel);
    }

    public void bindSocketToChannel(Socket socket, Provider provider) {}

    public NetworkChannel getCloudChannel() {
        return new NetworkChannel(new Identifier("THUNDER"), new Provider("DEFAULT"));
    }
}
