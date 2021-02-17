package de.lystx.cloudsystem.library.service.network.netty;

import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutVerifyConnection;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@Getter @Setter
public class NettyClient  {

    private String hostname;
    private int port;
    private final Map<Class<? extends Packet>, Integer> tries;
    private final PacketAdapter packetAdapter;
    private Channel channel;
    private boolean running;
    private Consumer<NettyClient> consumerConnection;
    private boolean established;

    public NettyClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.packetAdapter = new PacketAdapter();
        this.tries = new HashMap<>();
        this.established = false;
    }

    public void start() throws Exception {
        EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .group(workerGroup)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.IP_TOS, 24)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2));
                        channelPipeline.addLast(new LengthFieldPrepender(2));
                        channelPipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                        channelPipeline.addLast(new ObjectEncoder());

                        channelPipeline.addLast(new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                                if (packet instanceof PacketPlayOutVerifyConnection && !established) {
                                    established = true;
                                    consumerConnection.accept(NettyClient.this);
                                }
                                packetAdapter.handelAdapterHandler(packet);
                            }
                        });
                        channel = socketChannel;
                    }
                });

        try {
            ChannelFuture channelFuture = bootstrap.connect(this.hostname, port).sync();
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

    }

    public void onConnectionEstablish(Consumer<NettyClient> consumer) {
        this.consumerConnection = consumer;
    }

    public void sendPacket(Packet packet) {
        if (channel != null) {

            if (this.channel.eventLoop().inEventLoop()) {
                channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } else {
                try {
                    this.channel.eventLoop().execute(() -> channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE));
                } catch (NullPointerException ignored) {

                }
            }

        } else {
            int tries = this.tries.getOrDefault(packet.getClass(), 0);
            tries += 1;
            if (tries >= 5) {
                System.out.println("[NettyClient] Tried 5 times to send packet " + packet.getClass().getSimpleName() + " but connection refused!");
                this.tries.remove(packet.getClass());
                return;
            }
            this.tries.put(packet.getClass(), tries);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sendPacket(packet);
                }
            }, 1000L);
        }
    }


}
