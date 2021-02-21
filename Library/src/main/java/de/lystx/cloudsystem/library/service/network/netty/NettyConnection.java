package de.lystx.cloudsystem.library.service.network.netty;

import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Getter @Setter
public abstract class NettyConnection extends ChannelInitializer<SocketChannel> {

    protected Channel channel;
    protected boolean running;

    protected int port;
    protected String host;
    protected PacketAdapter packetAdapter;
    protected Consumer<NettyClient> consumerConnection;

    public NettyConnection(int port, String host, PacketAdapter packetAdapter) {
        this.port = port;
        this.host = host;
        this.packetAdapter = packetAdapter;

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    }

    public ChannelPipeline initChannelPipeline(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2));
        channelPipeline.addLast(new LengthFieldPrepender(2));
        channelPipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
        channelPipeline.addLast(new ObjectEncoder());
        return channelPipeline;
    }


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

    }

    public void onConnectionEstablish(Consumer<NettyClient> consumer) {
        this.consumerConnection = consumer;
    }

    public abstract void shutdown();

    public abstract void start() throws Exception;

    public abstract void sendPacket(Packet packet);
}
