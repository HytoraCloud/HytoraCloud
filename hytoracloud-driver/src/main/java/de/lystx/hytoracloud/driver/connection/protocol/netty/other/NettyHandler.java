package de.lystx.hytoracloud.driver.connection.protocol.netty.other;

import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class NettyHandler extends SimpleChannelInboundHandler<NettyPacket> {

    /**
     * The netty instance
     */
    private INetworkConnection networkConnection;


    @Override
    public void channelRead0(ChannelHandlerContext ctx, NettyPacket packet) throws Exception {
        if (this.networkConnection == null) {
            return;
        }
        this.networkConnection.getNetworkBus().processIn(ctx.channel(), packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getMessage() == null && !(cause instanceof IOException)) {
            cause.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // WHEN A CHANNEL CONNECTS TO THE SERVER
        // OR WHEN THE CLIENT CONNECTS TO THE SERVER

        // call handler event
        for (INetworkAdapter adapter : networkConnection.getNetworkAdapters()) {
            adapter.onChannelActive(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // WHEN A CHANNEL DISCONNECTS FROM THE SERVER
        // OR WHEN THE CLIENT DISCONNECTS FROM THE SERVER

        // call handler event
        for (INetworkAdapter adapter : networkConnection.getNetworkAdapters()) {
            adapter.onChannelInactive(ctx.channel());
        }
    }
}
