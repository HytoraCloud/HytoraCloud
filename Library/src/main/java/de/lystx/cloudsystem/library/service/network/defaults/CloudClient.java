package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.elements.other.NetworkHandler;
import de.lystx.cloudsystem.library.service.network.connection.adapter.AdapterHandler;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.client.connection.ConnectionClient;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

import java.util.LinkedList;
import java.util.List;

public class CloudClient {


    private final NetworkChannel networkChannel;
    private final AdapterHandler adapterHandler;
    private final List<NetworkHandler> networkHandlers;
    private ConnectionClient connectionClient;

    private final String host;
    private final Integer port;

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

    public void connect() {
        this.connectionClient = new ConnectionClient(this.adapterHandler, this.networkChannel, this.host, this.port, 5000);
    }

    public void disconnect() {
        this.connectionClient.stop();
    }

    public void sendPacket(Packet packet) {
        this.connectionClient.sendPacket(this.networkChannel, packet);
    }


    public void registerPacketHandler(Class<? extends Packet> packetClass, PacketHandlerAdapter packetHandlerAdapter) {
        this.adapterHandler.registerAdapter(packetHandlerAdapter);
    }

    public void registerPacketHandler(PacketHandlerAdapter packetHandlerAdapter) {
        this.adapterHandler.registerAdapter(packetHandlerAdapter);
    }

    public void registerHandler(NetworkHandler networkHandler) {
        this.networkHandlers.add(networkHandler);
    }

    public List<NetworkHandler> getNetworkHandlers() {
        return networkHandlers;
    }
}
