package de.lystx.cloudsystem.library.network.connection.client;

import de.lystx.cloudsystem.library.Cloud;
import de.lystx.cloudsystem.library.network.connection.NetworkInstance;
import de.lystx.cloudsystem.library.network.connection.server.NetworkServer;
import de.lystx.cloudsystem.library.network.extra.event.QueryEvent;
import de.lystx.cloudsystem.library.network.extra.exception.NettyOutputException;
import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.PacketMessenger;
import de.lystx.cloudsystem.library.service.event.CloudEventHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import de.lystx.cloudsystem.library.network.extra.util.PipelineUtil;
import de.lystx.cloudsystem.library.network.extra.util.Protocol;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * In a netty network this is the client which connects to the {@link NetworkServer}
 */
@Getter
public class NetworkClient extends NetworkInstance {

    /**
     * The netty bootstrap instance
     */
    private Bootstrap bootstrap;

    public NetworkClient(String host, int port, Logger logger) {
        super(host, port, logger);
        Cloud.getInstance().getEventService().registerEvent(this);
    }

    @Override
    public <T extends NetworkInstance> void build(Consumer<T> consumer) {

        getNetworkBus().getExecutors().execute(() -> {
            try {
                this.setup();
                this.connect();
                consumer.accept((T) this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @CloudEventHandler
    private void handleQuery(QueryEvent event) {
        if(!this.isConnected()){
            event.setCancelled(true);
            event.setCancelReason(new NettyOutputException(NettyOutputException.Type.CONNECTION_FAILED));
            return;
        }
        AbstractPacket packet = event.getToQueryPacket();

        PacketMessenger.transferToResponse(packet, event::accept);
    }


    /**
     * Sets the client up, that means initializing the network instance
     *
     * @see PipelineUtil
     * @see Bootstrap
     */
    @Override
    public NetworkClient setup() {
        this.eventExecutors = PipelineUtil.getEventLoopGroup();

        this.bootstrap = new Bootstrap()
                .group(eventExecutors)
                .channel(PipelineUtil.getChannel())
                .handler(PipelineUtil.getChannelInitializer(this, Protocol.VERSION));
        return this;
    }


    /**
     * Checks if the channel is connected (and so active)
     *
     * @return The result
     */
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    /**
     * Starts the client that means starting the {@link NioEventLoopGroup}
     * and connecting the {@link #bootstrap}
     *
     * @throws Exception If something goes wrong
     */
    public void connect() throws Exception {
        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        try {
            this.channel = bootstrap.connect(getHost(), getPort()).sync().channel();
            channel.closeFuture().sync().syncUninterruptibly();
        }
        finally {
            eventExecutors.shutdownGracefully();
        }
    }

    /**
     * Disconnects the channel ({@link #channel})
     *
     * @see io.netty.channel.Channel
     */
    public void disconnect() {
        channel.close();
        channel.disconnect();
    }

}
