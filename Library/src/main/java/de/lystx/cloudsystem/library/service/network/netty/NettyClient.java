package de.lystx.cloudsystem.library.service.network.netty;

import de.lystx.cloudsystem.library.elements.packets.out.PacketOutVerifyConnection;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketState;
import de.lystx.cloudsystem.library.service.server.other.process.Threader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@Getter @Setter
public class NettyClient {

    private String host;
    private int port;
    private final Map<Class<? extends Packet>, Integer> tries;
    private final PacketAdapter packetAdapter;
    private Channel channel;
    private boolean running;
    private Consumer<NettyClient> consumerConnection;
    private boolean established;


    public NettyClient(String hostname, int port) {
        this.host = hostname;
        this.port = port;
        this.packetAdapter = new PacketAdapter();
        this.tries = new HashMap<>();
        this.established = false;

    }

    /**
     * Starts the client with netty
     * @throws Exception
     */
    public void start() throws Exception {
        Threader.getInstance().execute(() -> {
            EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

            Bootstrap bootstrap;
            try {

                bootstrap = new Bootstrap()
                        .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                        .group(workerGroup)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.IP_TOS, 24)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline channelPipeline = socketChannel.pipeline();
                                channelPipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2));
                                channelPipeline.addLast(new LengthFieldPrepender(2));
                                channelPipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(NettyClient.class.getClassLoader())));
                                channelPipeline.addLast(new ObjectEncoder());

                                channelPipeline.addLast(new SimpleChannelInboundHandler<Packet>() {

                                    @Override
                                    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                        if (packet instanceof PacketOutVerifyConnection && !established && consumerConnection != null) {
                                            established = true;
                                            consumerConnection.accept(NettyClient.this);
                                        }
                                        packetAdapter.handelAdapterHandler(packet);
                                    }


                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        /*if (cause instanceof ClassNotFoundException) {
                                            return;
                                        }
                                        super.exceptionCaught(ctx, cause);*/
                                    }
                                });
                                channel = socketChannel;
                            }
                        });

            } catch (IllegalAccessError e) {
                bootstrap = null;
            }
            if (bootstrap == null) {
                System.out.println("[NettyClient] Couldn't build up Bootstrap for Client!");
                return;
            }
            try {
                ChannelFuture channelFuture = bootstrap.connect(this.host, port).sync();
                this.channel = channelFuture.channel();
                this.running = true;
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.running = false;
                this.established = false;
            } finally {
                this.running = false;
                this.established = false;
                workerGroup.shutdownGracefully();
            }
        });

    }

    /**
     * Adds consumer
     * @param consumer
     */
    public void onConnectionEstablish(Consumer<NettyClient> consumer) {
        this.consumerConnection = consumer;
    }

    /**
     * Sends a packet
     * If failed tries to send another 5 times then throws error
     * @param packet
     */
    public void sendPacket(Packet packet, Consumer<PacketState> consumer) {

        ChannelFutureListener listener = channelFuture -> {
            if (consumer == null) {
                return;
            }
            if (channelFuture.isSuccess()) {
                consumer.accept(PacketState.SUCCESS);
            } else {
                consumer.accept(PacketState.FAILED);
            }
        };

        if (channel != null) {
            if (this.channel.eventLoop().inEventLoop()) {
                channel.writeAndFlush(packet).addListener(listener).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } else {
                try {
                    this.channel.eventLoop().execute(() -> channel.writeAndFlush(packet).addListener(listener).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE));
                } catch (NullPointerException ignored) {
                    consumer.accept(PacketState.FAILED);
                }
            }
        } else {
            if (consumer != null) {
                consumer.accept(PacketState.NULL);
            }
        }
    }

    public void sendPacket(Packet packet) {
        this.sendPacket(packet, null);
    }

}