package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.service.network.connection.adapter.AdapterHandler;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.client.connection.ConnectionServer;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketHandler;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Setter @Getter
public class CloudServer implements CloudExecutor {

    private String host;
    private Integer port;

    private ConnectionServer server;
    private final NetworkChannel networkChannel;
    private final AdapterHandler adapterHandler;
    private final PacketHandler packetHandler;

    public CloudServer(String host, Integer port, NetworkChannel networkChannel, AdapterHandler adapterHandler, PacketHandler packetHandler) {
        this.host = host;
        this.port = port;

        this.networkChannel = networkChannel;
        this.adapterHandler = adapterHandler;
        this.packetHandler = packetHandler;

    }

    public void connect() {
        this.server = new ConnectionServer(this.adapterHandler, this.networkChannel, this.port);
    }

    public void disconnect() {
        try {
            this.server.stop();
        } catch (IOException e) {}
    }

    public void registerPacketHandler(Class<? extends Packet> packetClass, PacketHandlerAdapter packetHandlerAdapter) {
        this.adapterHandler.registerAdapter(packetHandlerAdapter);
    }

    public void registerPacket(Byte id, Class<? extends Packet> packet) {
        this.packetHandler.registerPacket(id, packet);
    }

    public void registerPacketHandler(PacketHandlerAdapter packetHandlerAdapter) {

        this.registerPacketHandler(null, packetHandlerAdapter);
    }

    public void sendPacket(Packet packet) {
        this.packetHandler.sendPacket(this.networkChannel, this.server, packet);
    }
}
