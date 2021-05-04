package de.lystx.cloudsystem.library.network.connection.server;

import de.lystx.cloudsystem.library.network.connection.NetworkInstance;
import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Boss handler of the netty communication
 */
public class BossHandler extends SimpleChannelInboundHandler<AbstractPacket> {

    /**
     * The handle of the netty instance
     */
    private final NetworkInstance handle;

    public BossHandler(NetworkInstance handle) {
        this.handle = handle;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AbstractPacket packet) throws Exception {
        if(handle == null) return;
        try {
            //TODO: CHECK OUT THIS
           // this.handle.getNetworkBus().processIn(ctx.channel(), packet);
        }
        finally {
            //packet.trySingleRelease();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handle.getLogger().warning("Netty Exception: " + cause.getMessage());
        cause.printStackTrace();
        if(cause.getMessage() == null) {
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // WHEN A CHANNEL CONNECTS TO THE SERVER
        // OR WHEN THE CLIENT CONNECTS TO THE SERVER

        // call handler event
        handle.callEvent(adapter -> adapter.handleChannelActive(ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // WHEN A CHANNEL DISCONNECTS FROM THE SERVER
        // OR WHEN THE CLIENT DISCONNECTS FROM THE SERVER

        // call handler event
        handle.callEvent(adapter -> adapter.handleChannelInActive(ctx.channel()));
    }
}
