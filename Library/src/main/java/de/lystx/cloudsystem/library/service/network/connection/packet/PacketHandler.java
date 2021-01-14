package de.lystx.cloudsystem.library.service.network.connection.packet;

import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.connection.client.connection.ConnectionClient;
import de.lystx.cloudsystem.library.service.network.connection.client.connection.ConnectionServer;

import java.util.HashMap;
import java.util.Map;

public class PacketHandler implements AbstractPacketHandler {

    private final Map<Byte, Class<? extends Packet>> registerdpackets;

    public PacketHandler() {
        this.registerdpackets = new HashMap<>();
    }

    public void registerPacket(Byte id, Class<? extends Packet> packet) {
        this.registerdpackets.put(id, packet);
    }

    public void unregisterPacket(Byte id) {
        this.registerdpackets.remove(id);
    }

    public void sendPacket(NetworkChannel networkChannel, ConnectionServer server, Packet packet) {
        server.sendPacket(networkChannel, packet);
    }

    public void sendPacket(NetworkChannel networkChannel, ConnectionClient client, Packet packet) {
        client.sendPacket(networkChannel, packet);
    }

    public Map<Byte, Class<? extends Packet>> getRegisterdpackets() {
        return this.registerdpackets;
    }
}
