package de.lystx.hytoracloud.networking.netty.codec.standard;

import de.lystx.hytoracloud.networking.exceptions.NetworkBadPacketException;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.PacketBuffer;
import de.lystx.hytoracloud.networking.provided.other.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public class PacketDecoder extends ByteToMessageDecoder {

    public PacketDecoder() {
        this.setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> output) {
        try {
            if (buf.readableBytes() == 0) {
                throw new NetworkBadPacketException("No readable bytes inside packet!");
            }
            PacketBuffer buffer = new PacketBuffer(buf, false);

            // read header from buffer
            // like the protocol version and id
            String _class = buffer.readString();
            UUID uniqueId = buffer.readUUID();
            long timestamp = buf.readLong();

            // builds the packets from the values
            // if the packet is null the constructor must be null
            AbstractPacket packet = NettyUtil.getInstance((Class<AbstractPacket>)Class.forName(_class));

            if (packet == null) {
                throw new NetworkBadPacketException("Received a Packet which couldn't be decoded! Packet returned null!(Maybe class " + _class + " is not instance of " + AbstractPacket.class.getName() + "!");
            }

            packet.setUniqueId(uniqueId);
            packet.setStamp(timestamp);
            packet.setChannel(ctx.channel());

            // makes the packets reads the payload from the packetbuffer
            try {
                packet.read(buffer);
            } catch (Exception e) {
                System.err.println("Error inside " + packet.getClass().getSimpleName() + "#read method: " + _class);
                e.printStackTrace();
            }
            output.add(packet);
        } catch (Exception e) {
            System.err.println("Error while decoding packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
