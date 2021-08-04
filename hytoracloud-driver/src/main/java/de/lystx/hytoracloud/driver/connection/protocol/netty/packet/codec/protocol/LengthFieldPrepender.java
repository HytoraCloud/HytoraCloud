package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.codec.protocol;

import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


@ChannelHandler.Sharable
public class LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {

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