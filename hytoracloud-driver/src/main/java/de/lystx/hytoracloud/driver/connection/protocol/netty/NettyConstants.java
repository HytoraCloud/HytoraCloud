package de.lystx.hytoracloud.driver.connection.protocol.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;

public class NettyConstants {

    //Factories
    public static final ChannelFactory<? extends ServerChannel> CHANNEL_FACTORY_SERVER = Epoll.isAvailable() ? EpollServerSocketChannel::new : KQueue.isAvailable() ? KQueueServerSocketChannel::new : NioServerSocketChannel::new;
    public static final ChannelFactory<? extends Channel> CHANNEL_FACTORY_CLIENT = Epoll.isAvailable() ? EpollSocketChannel::new : KQueue.isAvailable() ? KQueueSocketChannel::new : NioSocketChannel::new;

    //Other
    public static final EventLoopGroup EVENT_LOOP_GROUP = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    public static final EventLoopGroup WORKER_LOOP_GROUP = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

    //Channel
    public static final Class<? extends ServerChannel> CHANNEL_SERVER = Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    public static final Class<? extends Channel> CHANNEL_CLIENT = Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;

    //Initializer stuff
    public static final String INITIALIZER_FRAME_DECODER = "frame-decoder";
    public static final String INITIALIZER_FRAME_PREPENDER = "frame-prepender";
    public static final String INITIALIZER_PACKET_DECODER = "packets-decoder";
    public static final String INITIALIZER_PACKET_ENCODER = "packets-encoder";
    public static final String INITIALIZER_BOSS_HANDLER = "boss-handler";

}
