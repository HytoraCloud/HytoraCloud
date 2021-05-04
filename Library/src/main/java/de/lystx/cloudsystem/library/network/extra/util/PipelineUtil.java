package de.lystx.cloudsystem.library.network.extra.util;

import de.lystx.cloudsystem.library.network.connection.NetworkInstance;
import de.lystx.cloudsystem.library.network.packet.codec.varint.Varint32FrameDecoder;
import de.lystx.cloudsystem.library.network.packet.codec.varint.Varint32LengthFieldPrepender;
import de.lystx.cloudsystem.library.network.packet.codec.standard.PacketDecoder;
import de.lystx.cloudsystem.library.network.packet.codec.standard.PacketEncoder;
import de.lystx.cloudsystem.library.network.connection.server.BossHandler;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class PipelineUtil {

    public static final String DEFLATER = "deflater";
    public static final String INFLATER = "inflater";
    public static final String FRAME_DECODER = "frame-decoder";
    public static final String FRAME_PREPENDER = "frame-prepender";
    public static final String PACKET_DECODER = "packets-decoder";
    public static final String PACKET_ENCODER = "packets-encoder";
    public static final String BOSS_HANDLER = "boss-handler";

    /**
     * Epoll = pipeline improvement at linux
     */
    private static boolean epoll = false;

    static {
        if(!System.getProperty("os.name").toLowerCase().contains("win")) {
            epoll = Epoll.isAvailable();

            if(!epoll) {
                System.err.println("Despite being on Unix epoll is not working, falling back to NIO. (" + Epoll.unavailabilityCause().getMessage() + ")");
            }
        }
    }

    /**
     * Gets the channel for the server<br>
     * If epoll is available then choose the epoll type of channel
     *
     * @return The serverChannel
     */
    public static Class<? extends ServerChannel> getServerChannel() {
        return epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    /**
     * Gets the channel for the client<br>
     * If epoll is available then choose the epoll type of channel
     *
     * @return The serverChannel
     */
    public static Class<? extends Channel> getChannel() {
        return epoll ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    /**
     * Gets the event loop group<br>
     * If epoll is available then choose the epoll type of event loop group
     *
     * @return The serverChannel
     */
    public static EventLoopGroup getEventLoopGroup() {
        return epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    /**
     * Gets the channel initializer for a netty channel
     *
     * @param instance        The netty instance
     * @param protocolVersion The protocol version of this instance
     * @return The initializer object
     */
    public static ChannelInitializer<Channel> getChannelInitializer(NetworkInstance instance, int protocolVersion) {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                // Compression of stream
                /*pipeline.addLast(DEFLATER, new JZlibEncoder(ZlibWrapper.GZIP));
                pipeline.addLast(INFLATER, new JZlibDecoder(ZlibWrapper.GZIP));*/

                // Codec
                pipeline.addLast(FRAME_DECODER, new Varint32FrameDecoder());

                // Packet decoder
                PacketDecoder decoder = new PacketDecoder(instance);
                decoder.setProtocolVersion(protocolVersion);
                pipeline.addLast(PACKET_DECODER, decoder);

                pipeline.addLast(FRAME_PREPENDER, new Varint32LengthFieldPrepender());

                // Packet encoder
                PacketEncoder encoder = new PacketEncoder(instance);
                encoder.setProtocolVersion(protocolVersion);
                pipeline.addLast(PACKET_ENCODER, encoder);

                // Handler
                pipeline.addLast(BOSS_HANDLER, new BossHandler(instance));
            }
        };
    }

}
