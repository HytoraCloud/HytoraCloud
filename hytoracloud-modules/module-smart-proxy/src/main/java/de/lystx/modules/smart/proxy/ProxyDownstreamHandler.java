package de.lystx.modules.smart.proxy;

import de.lystx.hytoracloud.driver.CloudDriver;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@Getter @AllArgsConstructor
public class ProxyDownstreamHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * The channel of this handler
     */
    private final Channel channel;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.channel.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        this.channel.writeAndFlush(buf.retain());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            return;
        }
        CloudDriver.getInstance().log("ProxyDownStreamHandler", "§cException was caught:");
        cause.printStackTrace();
    }

}
