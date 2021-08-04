package de.lystx.hytoracloud.driver.connection.protocol.netty.packet;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

@Getter
@NoArgsConstructor
public abstract class NettyPacket implements IPacket {

    /**
     * The version of the protocol
     */
    @Setter
    protected int protocolVersion = 0;

    /**
     * The unique query id (to determine request-response system)
     */
    @Setter
    protected UUID uniqueId;

    /**
     * The timestamp of the time of the sending
     */
    @Setter
    protected long stamp = -1;

    /**
     * The channel of the packet being
     */
    @Setter
    protected Channel channel;

    /**
     * The byte buf (originally the raw form of the packet)
     */
    @Setter
    protected ByteBuf buf;

    /**
     * Converts given bytebuffer into this packets
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong lel
     */
    public abstract void read(PacketBuffer buf) throws IOException;

    /**
     * Converts this packets into given bytebuffer
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong lel
     */
    public abstract void write(PacketBuffer buf) throws IOException;

    /**
     * Get the address of the channel
     *
     * @return The address
     */
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) getChannel().remoteAddress();
    }

}
