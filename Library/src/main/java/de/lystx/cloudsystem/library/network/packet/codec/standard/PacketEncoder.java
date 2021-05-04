package de.lystx.cloudsystem.library.network.packet.codec.standard;

import de.lystx.cloudsystem.library.network.connection.NetworkInstance;
import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.PacketBuffer;
import de.lystx.cloudsystem.library.network.packet.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;

/**
 * Encodes a {@link AbstractPacket}
 */
public class PacketEncoder extends MessageToByteEncoder<AbstractPacket> {

    /**
     * The netty instance
     */
    private NetworkInstance main;

    /**
     * The protocolVersion. Default value is -1, but would mean a failure
     */
    @Setter
    private int protocolVersion = -1;

    public PacketEncoder(NetworkInstance main) {
        this.main = main;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractPacket packet, ByteBuf output) {
        try {
            PacketBuffer buffer = new PacketBuffer(output);

            int protocolId = PacketRegistry.getInstance().getId(packet.getClass());
            if(protocolId == -1) {
                throw new Exception("Couldn't find packet! (id: " + protocolId + ")");
            }
            packet.protocolVersion = protocolVersion;
            packet.protocolId = protocolId;

            // Writes important values to the header of the packets
            // example is the protocol version and id
            // and the queryuid which is used to determine the pipeline between request/response
            buffer.writeVarInt(protocolVersion);
            buffer.writeVarInt(protocolId);
            buffer.writeUUID(packet.uniqueId);
            buffer.writeLong(packet.stamp);

            // message
            try {
                packet.write(buffer);
            }
            catch(Exception e) {
                output.clear().release();

                System.err.println("Error inside " + packet.getClass().getSimpleName() + "#write method: " + e.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        catch(Exception e) {
            System.err.println("Error while encoding " + packet.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
