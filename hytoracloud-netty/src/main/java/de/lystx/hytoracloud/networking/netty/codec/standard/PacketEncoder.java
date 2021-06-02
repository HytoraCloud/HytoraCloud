package de.lystx.hytoracloud.networking.netty.codec.standard;

import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;

public class PacketEncoder extends MessageToByteEncoder<AbstractPacket> {


    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractPacket packet, ByteBuf output) {
        try {
            PacketBuffer buffer = new PacketBuffer(output, false);


            // Writes important values to the header of the packets
            // example is the protocol version and id
            // and the uuid which is used to determine the pipeline between request/response
            buffer.writeString(packet.getClass().getName());
            buffer.writeUUID(packet.getUniqueId());
            buffer.writeLong(packet.getStamp());

            // message
            try {
                packet.write(buffer);
            } catch(Exception e) {
                output.clear().release();
                System.err.println("Error inside " + packet.getClass().getSimpleName() + "#write method: " + e.getClass().getSimpleName());
                e.printStackTrace();
            }
        } catch(Exception e) {
            System.err.println("Error while encoding " + packet.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
