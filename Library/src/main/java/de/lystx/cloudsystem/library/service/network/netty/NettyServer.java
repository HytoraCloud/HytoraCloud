package de.lystx.cloudsystem.library.service.network.netty;

import de.lystx.cloudsystem.library.elements.packets.out.PacketOutVerifyConnection;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketState;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Getter @Setter
public class NettyServer {

    private int port;
    private String host;
    private final PacketAdapter packetAdapter;
    private final List<Channel> registeredChannels;
    private Channel channel;
    private boolean running;

    public NettyServer(String host, int port) {
        this.port = port;
        this.host = host;
        this.running = false;
        this.packetAdapter = new PacketAdapter();
        this.registeredChannels = new LinkedList<>();
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    }

    /**
     * Starts server with netty
     */
    public void start() {

        EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        EventLoopGroup bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(workerGroup, bossGroup);
        serverBootstrap.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline channelPipeline = socketChannel.pipeline();
                channelPipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2));
                channelPipeline.addLast(new LengthFieldPrepender(2));
                channelPipeline.addLast( new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));

                channelPipeline.addLast(new ObjectEncoder());
                channelPipeline.addLast(new SimpleChannelInboundHandler<Packet>() {
                    @Override
                    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                        packetAdapter.handelAdapterHandler(packet);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        if (cause instanceof IOException) {
                            return;
                        }
                        cause.printStackTrace();
                    }
                });
                registeredChannels.add(socketChannel);
                socketChannel.writeAndFlush(new PacketOutVerifyConnection(socketChannel.localAddress().getAddress().getHostAddress(), socketChannel.localAddress().getPort()));
                //System.out.println("[NettyServer] Initialized NettyClient > " + socketChannel);
            }
        });


        try {
            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
            this.channel = channelFuture.channel();
            this.running = true;
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
            this.running = false;
        } finally {
            this.running = false;
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    /**
     * Sends packet
     * @param packet
     */
    public void sendPacket(Packet packet) {
        this.sendPacket(packet, null);
    }


    /**
     * Sends packet
     * @param packet
     */
    public void sendPacket(Packet packet, Consumer<PacketState> consumer) {
        ChannelFutureListener listener = channelFuture -> {
            if (consumer == null) {
                return;
            }
            try {
                if (channelFuture.isSuccess()) {
                    consumer.accept(PacketState.SUCCESS);
                } else {
                    consumer.accept(PacketState.FAILED);
                }
            } catch (NullPointerException e) {
                consumer.accept(PacketState.NULL);
            }
        };
        for (Channel registeredChannel : this.registeredChannels) {
            registeredChannel.writeAndFlush(packet).addListener(listener);
        }
    }

}