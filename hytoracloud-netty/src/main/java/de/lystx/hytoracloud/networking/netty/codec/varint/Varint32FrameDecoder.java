package de.lystx.hytoracloud.networking.netty.codec.varint;

import com.google.protobuf.CodedInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

/**
 * Old version of the frame decoder of netty (the newer version didn't work with bungee/spigot)
 */
public class Varint32FrameDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramByteBuf, List<Object> paramList) throws Exception {
        paramByteBuf.markReaderIndex();
        byte[] arrayOfByte = new byte[5];
        for(int i = 0; i < arrayOfByte.length; i++) {
            if(!paramByteBuf.isReadable()) {
                paramByteBuf.resetReaderIndex();
                return;
            }
            arrayOfByte[i] = paramByteBuf.readByte();
            if(arrayOfByte[i] >= 0) {
                int j = CodedInputStream.newInstance(arrayOfByte, 0, i + 1).readRawVarint32();
                if(j < 0) {
                    throw new CorruptedFrameException("negative length: " + j);
                }
                if(paramByteBuf.readableBytes() < j) {
                    paramByteBuf.resetReaderIndex();
                    return;
                }
                paramList.add(paramByteBuf.readBytes(j));
                return;
            }
        }
        throw new CorruptedFrameException("length wider than 32-bit");
    }
}
