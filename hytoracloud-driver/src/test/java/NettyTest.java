
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.val;

import java.net.InetSocketAddress;

public class NettyTest {

    private static class EchoServer {

        private final int port;
        private ChannelFuture channelFuture;

        private EchoServer(int port) {
            this.port = port;
        }

        public void start() throws Exception {
            NioEventLoopGroup group = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(group)
                        .channel(NioServerSocketChannel.class)
                        .localAddress(new InetSocketAddress(port))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                //ch.pipeline().addLast(new EchoServerHandler());
                            }
                        });

                channelFuture = b.bind().sync();
                System.out.println(EchoServer.class.getName() + " started and listen on " + channelFuture.channel().localAddress());
            } finally {
                channelFuture.channel().close();
                channelFuture.channel().closeFuture().sync();
                group.shutdownGracefully().sync();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        new EchoServer(2020).start();
        System.out.println("Hello Hello");
    }
}
