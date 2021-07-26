package de.lystx.modules.smart.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.modules.smart.SmartProxy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class ProxyNettyServer {

    /**
     * The host of this server
     */
    private final String host;

    /**
     * The port of this server
     */
    private final int port;

    /**
     * The boss group
     */
    private final EventLoopGroup bossGroup;

    /**
     * The worker group
     */
    private final EventLoopGroup workerGroup;

    public ProxyNettyServer(String host, int port) {
        this.host = host;
        this.port = port;

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = SmartProxy.getInstance().getWorkerGroup();
    }

    /**
     * Binds this server
     *
     * @throws Exception if something goes wrong
     */
    public void bind() throws Exception {
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel channel) throws Exception {
                            if (!SmartProxy.getInstance().isEnabled()) {
                                return;
                            }
                            channel.pipeline().addLast("minecraftdecoder", new PacketDecoder());
                        }
                    })
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class).bind(this.host, this.port).sync();

            SmartProxy.getInstance().setChannel(channelFuture.channel());
            CloudDriver.getInstance().log("SmartProxy", "§7Successfully started §bnetty-server §7on §3" + host + "§h:§3" + port + "§h!");

            try {
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                //Socket is closed
                CloudDriver.getInstance().log("SmartProxy", "§7Closed §bnetty-server§h!");
            }
        } finally {
            //Shutting down all groups
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}