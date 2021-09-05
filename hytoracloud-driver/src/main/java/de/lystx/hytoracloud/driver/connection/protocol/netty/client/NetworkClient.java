package de.lystx.hytoracloud.driver.connection.protocol.netty.client;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.DriverInfo;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.NettyConstants;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel.INetworkChannel;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.DefaultNettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.INettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification.ConnectionType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.handling.INetworkAdapter;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.NetworkInstance;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.PacketClientCredentials;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.NetworkServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * In a netty network this is the client which connects to the {@link NetworkServer}
 */
@Getter
public class NetworkClient extends NetworkInstance implements INetworkClient {


    /**
     * The name of the client
     */
    @Setter
    private String username;

    /**
     * The info
     */
    private final INettyClient nettyClient;

    public NetworkClient(String host, int port, ConnectionType type, String username) {
        super(host, port, type);

        this.username = username;

        this.nettyClient = new DefaultNettyClient(username, host, port, type, null);

        this.registerNetworkAdapter(new INetworkAdapter() {
            @Override
            public void onPacketReceive(IPacket packet) {}

            @Override
            public void onHandshakeReceive(PacketHandshake handshake) {
                sendPacket(new PacketClientCredentials(nettyClient));
            }

            @Override
            public void onPacketSend(IPacket packet) {}

            @Override
            public void onChannelActive(INetworkChannel channel) {}

            @Override
            public void onChannelInactive(INetworkChannel channel) {}
        });
    }


    @Override
    public void bootstrap() {

        try {
            NioEventLoopGroup eventExecutors = new NioEventLoopGroup(1);
            Bootstrap bootstrap = new Bootstrap()
                    .group(bossGroup)
                    //.channelFactory(NettyConstants.CHANNEL_FACTORY_CLIENT)
                    .channel(NettyConstants.CHANNEL_CLIENT)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(getChannelInitializer(CloudDriver.class.getAnnotation(DriverInfo.class).packetProtocolVersion()));

            try {
                channel = bootstrap.connect(host, port).sync().channel();
                channel.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                eventExecutors.shutdownGracefully();
                bossGroup.shutdownGracefully();

                channel.close().sync();
                channel.closeFuture().sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPacket(INetworkConnection connection, IPacket packet) {
    }

    @Override
    public void shutdown() {
        channel.close();
        channel.disconnect();
        bossGroup.shutdownGracefully();
    }

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(getHost(), getPort());
    }

}
