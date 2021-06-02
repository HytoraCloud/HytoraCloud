package de.lystx.hytoracloud.networking.connection.base;

import de.lystx.hytoracloud.driver.service.event.CloudEventHandler;
import de.lystx.hytoracloud.driver.service.event.IEventService;
import de.lystx.hytoracloud.networking.connection.NetworkExecutor;
import de.lystx.hytoracloud.networking.events.CloudPacketQueryEvent;
import de.lystx.hytoracloud.networking.exceptions.NetworkGatewayOutputException;
import de.lystx.hytoracloud.networking.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.networking.provided.objects.NetworkEventAdapter;
import de.lystx.hytoracloud.networking.provided.other.NettyUtil;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.manager.PacketMessenger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * In a netty network this is the client which connects to the {@link NetworkServer}
 */
@Getter
public class NetworkClient extends NetworkExecutor<NetworkClient> {

    /**
     * The netty bootstrap instance
     */
    private Bootstrap bootstrap;

    public NetworkClient(String host, int port, IEventService eventService) {
        super(host, port,  eventService);
    }

    @Override
    public void build(Consumer<NetworkClient> consumer, Consumer<Exception> exceptionConsumer) {
        new Thread(() -> {
            try {
                this.registerEventAdapter(new NetworkEventAdapter() {
                    @Override
                    public void handlePacketReceive(AbstractPacket packet) {
                    }

                    @Override
                    public void handleHandshake(PacketHandshake handshake) {
                    }

                    @Override
                    public void handlePacketSend(AbstractPacket packet) {
                    }

                    @Override
                    public void handleChannelActive(Channel channel) {
                        consumer.accept(NetworkClient.this);
                    }

                    @Override
                    public void handleChannelInActive(Channel channel) {
                    }
                });
                this.setup().connect();
            } catch (Exception e) {
                exceptionConsumer.accept(e);
            }
        }, "network_client").start();
    }

    @Override
    public void sendPacket(AbstractPacket packet, Channel channel) {
        this.networkGateway.prepareGate(channel, packet);
    }

    @Override
    public void sendPacket(AbstractPacket packet) {
        this.sendPacket(packet, channel);
    }


    @CloudEventHandler
    private void handleQuery(CloudPacketQueryEvent event) {
        if(!this.isConnected()){
            event.setCancelled(true);
            event.setCancelReason(new NetworkGatewayOutputException(NetworkGatewayOutputException.Type.CONNECTION_FAILED));
            return;
        }
        AbstractPacket packet = event.getToQueryPacket();

        PacketMessenger.transferToResponse(packet, event::accept);
    }


    /**
     * Sets the client up, that means initializing the network instance
     *
     * @see NettyUtil
     * @see Bootstrap
     */
    @Override
    public NetworkClient setup() {
        this.eventLoopGroup = NettyUtil.getEventLoopGroup();

        this.bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NettyUtil.getChannel())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        createPipeline(socketChannel);
                    }
                });
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
        try {
            EventLoopGroup eventExecutors = new NioEventLoopGroup();
            try {
                channel = bootstrap.connect(getHost(), getPort()).sync().channel();
                channel.closeFuture().sync().syncUninterruptibly();
            } finally {
                eventExecutors.shutdownGracefully();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
