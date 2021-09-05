package de.lystx.hytoracloud.driver.connection.protocol.netty.global.handling;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel.INetworkChannel;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.PacketHandshake;

/**
 * This adapter is for listening to important network instance events
 */
public interface INetworkAdapter {

    /**
     * When the instance receives a handshake packet
     *
     * @param handshake The handshake packet
     */
    void onHandshakeReceive(PacketHandshake handshake);

    /**
     * When the instance connected to the server or when a client connected to the instance
     *
     * @param channel The channel
     */
    void onChannelActive(INetworkChannel channel);

    /**
     * When the instance disconnected from the server or when a client disconnected from the instance
     *
     * @param channel The channel
     */
    void onChannelInactive(INetworkChannel channel);

    /**
     * When the instance wants to send a packet
     *
     * @param packet The packet
     */
    void onPacketSend(IPacket packet);

    /**
     * When the instance receives a packet
     *
     * @param packet The packet
     */
    void onPacketReceive(IPacket packet);

}
