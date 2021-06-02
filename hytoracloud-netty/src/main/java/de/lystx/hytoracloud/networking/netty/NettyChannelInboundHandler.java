package de.lystx.hytoracloud.networking.netty;

import de.lystx.hytoracloud.networking.connection.NetworkExecutor;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;

import java.io.IOException;


@AllArgsConstructor
public class NettyChannelInboundHandler extends SimpleChannelInboundHandler<AbstractPacket> {

    /**
     * The handle of the netty instance
     */
    private final NetworkExecutor<?> handle;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AbstractPacket packet) throws Exception {
        try {
           this.handle.getNetworkGateway().openGate(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        if (e.getMessage() != null && e.getMessage().contains("Eine vorhandene Verbindung wurde vom Remotehost geschlossen")) {
            return;
        }
        e.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // call handler event
        handle.callEvent(adapter -> adapter.handleChannelActive(ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // call handler event
        handle.callEvent(adapter -> adapter.handleChannelInActive(ctx.channel()));
    }
}
