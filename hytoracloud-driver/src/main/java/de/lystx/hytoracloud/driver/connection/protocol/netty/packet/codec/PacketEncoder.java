package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.codec;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;


public class PacketEncoder extends MessageToByteEncoder<NettyPacket> {

    /**
     * The protocolVersion. Default value is -1, but would mean a failure
     */
    @Setter
    private int protocolVersion = -1;

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyPacket packet, ByteBuf output) {
        try {
            PacketBuffer buffer = new PacketBuffer(output);

            packet.setProtocolVersion(protocolVersion);

            buffer.writeString(packet.getClass().getName());
            buffer.writeVarInt(protocolVersion);
            buffer.writeUUID(packet.getUniqueId());
            buffer.writeLong(packet.getStamp());

            // message
            try {
                packet.write(buffer);
            } catch(Exception e) {
                output.clear().release();
                System.err.println("Error inside " + packet.getClass().getName()  + "#write method: " + e.getClass().getSimpleName());
                e.printStackTrace();
            }
        } catch(Exception e) {
            System.err.println("Error while encoding " + packet.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
