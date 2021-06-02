package de.lystx.hytoracloud.networking.netty.codec.varint;

import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Old version of the field prepender of netty (the newer version didn't work with bungee/spigot)
 */
@ChannelHandler.Sharable
public class Varint32LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {

    protected void encode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramByteBuf1, ByteBuf paramByteBuf2) throws Exception {
        int i = paramByteBuf1.readableBytes();
        int j = CodedOutputStream.computeRawVarint32Size(i);
        paramByteBuf2.ensureWritable(j + i);

        CodedOutputStream localCodedOutputStream = CodedOutputStream.newInstance(new ByteBufOutputStream(paramByteBuf2), j);

        localCodedOutputStream.writeRawVarint32(i);
        localCodedOutputStream.flush();

        paramByteBuf2.writeBytes(paramByteBuf1, paramByteBuf1.readerIndex(), i);
    }
}