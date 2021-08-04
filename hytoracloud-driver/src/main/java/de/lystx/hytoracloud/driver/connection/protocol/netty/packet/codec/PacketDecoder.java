package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.codec;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.BadPacketException;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public class PacketDecoder extends ByteToMessageDecoder {

    /**
     * The protocolVersion. Default value is -1, but would mean a failure
     */
    @Setter
    private int protocolVersion = -1;

    public PacketDecoder() {
        this.setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> output) {
        try {
            PacketBuffer buffer = new PacketBuffer(buf);
            if (buf.readableBytes() == 0) {
                throw new BadPacketException("No readable bytes inside packet!");
            }


            // read header from buffer
            String name = buffer.readString();
            int protocolVersion = buffer.readVarInt();
            UUID uniqueId = buffer.readUUID();
            long timestamp = buffer.readLong();

            // check protocol
            if (this.protocolVersion != protocolVersion) {
                buf.skipBytes(buf.readableBytes());
                throw new BadPacketException("Received packets with wrong protocol version! " + protocolVersion + " instead of " + this.protocolVersion);
            }
            Class<? extends NettyPacket> pClass = (Class<? extends NettyPacket>) Class.forName(name);

            // builds the packets from the values
            // if the packet is null the constructor must be null
            NettyPacket packet;

            try {
                packet = pClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                packet = Utils.getInstance(pClass);
            }

            if (packet == null) {
                throw new NullPointerException("Packet is null because there is no NoArgsConstructor inside " + pClass.getSimpleName() + "!");
            }
            packet.setProtocolVersion(protocolVersion);
            packet.setUniqueId(uniqueId);
            packet.setStamp(timestamp);
            packet.setChannel(ctx.channel());
            packet.setBuf(buf);

            // makes the packets reads the payload from the packetbuffer
            try {
                packet.read(buffer);
            } catch(Exception e) {
                System.err.println("Error inside " + pClass.getName() + "#read method: " + e.getClass().getSimpleName());
                e.printStackTrace();
            }
            output.add(packet);
        } catch(Exception e) {
            System.err.println("Error while decoding packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
