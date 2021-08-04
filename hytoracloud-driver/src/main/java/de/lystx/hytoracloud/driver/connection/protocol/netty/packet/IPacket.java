package de.lystx.hytoracloud.driver.connection.protocol.netty.packet;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

public interface IPacket {

    /**
     * The {@link UUID} of the packet
     *
     * @return uuid
     */
    UUID getUniqueId();

    /**
     * The netty {@link Channel} of this packet
     *
     * @return channel
     */
    Channel getChannel();

    /**
     * The time when it was sent
     *
     * @return long millis
     */
    long getStamp();

    default long getDif() {
        return System.currentTimeMillis() - getStamp();
    }

    /**
     * Get the address of the channel
     *
     * @return address
     */
    InetSocketAddress getAddress();
    /**
     * The buf where all the data is stored
     *
     * @return buf
     */
    ByteBuf getBuf();

    /**
     * The protocolVersion to identify packets
     *
     * @return id as int
     */
    int getProtocolVersion();

    /**
     * Converts given bytebuffer into this packets
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong lel
     */
    void read(PacketBuffer buf) throws IOException;

    /**
     * Converts this packets into given bytebuffer
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong lel
     */
    void write(PacketBuffer buf) throws IOException;

}
