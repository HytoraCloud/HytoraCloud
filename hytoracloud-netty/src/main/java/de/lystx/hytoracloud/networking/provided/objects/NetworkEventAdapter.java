package de.lystx.hytoracloud.networking.provided.objects;

import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.impl.PacketHandshake;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * This adapter is for listening to important network instance events
 */
public interface NetworkEventAdapter {

    /**
     * When the instance receives a packet
     *
     * @param packet The packet
     */
    void handlePacketReceive(AbstractPacket packet);

    /**
     * When the instance receives a handshake packet
     *
     * @param handshake The handshake packet
     */
    void handleHandshake(PacketHandshake handshake);

    /**
     * When the instance wants to send a packet
     *
     * @param packet The packet
     */
    void handlePacketSend(AbstractPacket packet);

    /**
     * When the instance connected to the server or when a client connected to the instance
     *
     * @param channel The channel
     */
    void handleChannelActive(Channel channel);

    /**
     * When the instance disconnected from the server or when a client disconnected from the instance
     *
     * @param channel The channel
     */
    void handleChannelInActive(Channel channel);

}
