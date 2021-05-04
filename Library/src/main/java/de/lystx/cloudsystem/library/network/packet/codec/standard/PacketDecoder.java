package de.lystx.cloudsystem.library.network.packet.codec.standard;

import de.lystx.cloudsystem.library.network.connection.NetworkInstance;
import de.lystx.cloudsystem.library.network.extra.exception.BadPacketException;
import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.PacketBuffer;
import de.lystx.cloudsystem.library.network.packet.PacketRegistry;
import de.lystx.cloudsystem.library.network.extra.util.ReflectionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Decodes a {@link AbstractPacket}
 */
public class PacketDecoder extends ByteToMessageDecoder {

    /**
     * The netty instance
     */
    private NetworkInstance main;

    /**
     * The protocolVersion. Default value is -1, but would mean a failure
     */
    @Setter
    private int protocolVersion = -1;

    public PacketDecoder(NetworkInstance main) {
        this.setSingleDecode(true);
        this.main = main;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> output) {
        try {
            if(buf.readableBytes() == 0) {
                throw new BadPacketException("No readable bytes inside packet!");
            }

            PacketBuffer buffer = new PacketBuffer(buf);

            // read header from buffer
            // like the protocol version and id
            int protocolVersion = buffer.readVarInt();
            int protocolId = buffer.readVarInt();
            UUID queryUid = buffer.readUUID();
            long timestamp = buffer.readLong();

            // check protocol
            if(this.protocolVersion != protocolVersion) {
                buf.skipBytes(buf.readableBytes());
                throw new BadPacketException("Received packets with wrong protocol version! " + protocolVersion + " instead of " + this.protocolVersion);
            }
            Class<? extends AbstractPacket> pClass = PacketRegistry.getInstance().getPacket(protocolId);
            if(pClass == null) {
                buf.skipBytes(buf.readableBytes());
                throw new BadPacketException("Cannot receive unregistered packets! (id:" + protocolId + ")");
            }

            // builds the packets from the values
            // if the packet is null the constructor must be null
            AbstractPacket packet = (AbstractPacket) ReflectionUtil.getInstance(pClass);
            if (packet == null) {
                throw new NullPointerException("Packet is null because there is no NoArgsConstructor inside " + pClass.getSimpleName() + "!");
            }
            packet.protocolVersion = protocolVersion;
            packet.protocolId = protocolId;
            packet.uniqueId = queryUid;
            packet.stamp = timestamp;
            packet.channel = ctx.channel();
            packet.buf = buf;

            // makes the packets reads the payload from the packetbuffer
            try {
                packet.read(buffer);
            }
            catch(Exception e) {
                System.err.println("Error inside " + packet.getClass().getSimpleName() + "#read method: " + e.getClass().getSimpleName());
                e.printStackTrace();
            }
            output.add(packet);
        }
        catch(Exception e) {
            System.err.println("Error while decoding packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
