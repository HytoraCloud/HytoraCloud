package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.elements.other.NetworkHandler;
import de.lystx.cloudsystem.library.service.network.connection.adapter.AdapterHandler;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.client.connection.ConnectionClient;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.ConnectException;
import java.util.LinkedList;
import java.util.List;

@Setter @Getter
public class CloudClient implements CloudExecutor {


    private final NetworkChannel networkChannel;
    private final AdapterHandler adapterHandler;
    private final List<NetworkHandler> networkHandlers;
    private ConnectionClient connectionClient;

    private String host;
    private Integer port;

    public CloudClient(String host, Integer port, NetworkChannel networkChannel, AdapterHandler adapterHandler) {
        this.networkChannel = networkChannel;
        this.adapterHandler = adapterHandler;
        this.networkHandlers = new LinkedList<>();
        this.host = host;
        this.port = port;

    }

    public AdapterHandler getAdapterHandler() {
        return adapterHandler;
    }

    public ConnectionClient getConnectionClient() {
        return connectionClient;
    }

    public void connect() throws IOException, ConnectException {
        this.connect(this.host, this.port);
    }

    public void connect(String host, int port) throws IOException, ConnectException {
        this.connectionClient = new ConnectionClient(this.adapterHandler, this.networkChannel, host, port, 5000);
    }

    public boolean isConnected() {
        return (this.connectionClient != null);
    }

    public void disconnect() {
        this.connectionClient.stop();
    }

    public void sendPacket(Packet packet) {
        this.connectionClient.sendPacket(this.networkChannel, packet);
    }

    public void registerPacketHandler(Object packetHandlerAdapter) {
        this.adapterHandler.registerAdapter(packetHandlerAdapter);
    }

    public void registerHandler(NetworkHandler networkHandler) {
        this.networkHandlers.add(networkHandler);
    }

    public List<NetworkHandler> getNetworkHandlers() {
        return networkHandlers;
    }
}
