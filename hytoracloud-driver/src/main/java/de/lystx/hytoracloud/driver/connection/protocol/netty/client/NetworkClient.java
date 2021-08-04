package de.lystx.hytoracloud.driver.connection.protocol.netty.client;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.DriverInfo;
import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkBus;
import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.DefaultNettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.INettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.ClientType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.NetworkBusObject;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.INetworkAdapter;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.PacketClientCredentials;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.NetworkServer;
import de.lystx.hytoracloud.driver.utils.other.PipelineUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * In a netty network this is the client which connects to the {@link NetworkServer}
 */
@Getter
public class NetworkClient extends Thread implements INetworkClient {

    /**
     * The netty bootstrap instance
     */
    private Bootstrap bootstrap;

    /**
     * Value if the client is authenticated
     */
    @Setter
    private boolean authenticated = false;

    /**
     * The host of the server
     */
    private final String host;

    /**
     * The port of the server
     */
    private final int port;

    /**
     * The event handler from netty
     */
    private EventLoopGroup eventLoopGroup;

    /**
     * The network bus for handling incoming and outgoing connections
     */
    private final INetworkBus networkBus;

    /**
     * The netty channel of the instance
     */
    @Setter
    private Channel channel;

    /**
     * All registered adapters
     */
    private final List<INetworkAdapter> networkAdapters;

    /**
     * All registered packet handlers
     */
    private final List<IPacketHandler> packetHandlers;

    /**
     * The name of the client
     */
    private final String username;

    /**
     * The type
     */
    private final ClientType type;

    /**
     * The info
     */
    private final INettyClient nettyClient;

    public NetworkClient(String host, int port, ClientType type, String username) {
        this.host = host;
        this.port = port;

        this.type = type;
        this.username = username;

        this.nettyClient = new DefaultNettyClient(username, host, port, type, null);

        this.networkAdapters = new LinkedList<>();
        this.packetHandlers = new LinkedList<>();
        this.networkBus = new NetworkBusObject(this);

        this.registerNetworkAdapter(new INetworkAdapter() {
            @Override
            public void onPacketReceive(IPacket packet) {
            }

            @Override
            public void onHandshakeReceive(PacketHandshake handshake) {
            }

            @Override
            public void onPacketSend(IPacket packet) {
            }

            @Override
            public void onChannelActive(Channel channel) {
                sendPacket(new PacketClientCredentials(nettyClient));
            }

            @Override
            public void onChannelInactive(Channel channel) {
            }
        });
    }

    @Override
    public void bootstrap() throws Exception {
        this.start();
    }

    @Override
    public void sendPacket(INetworkConnection connection, IPacket packet) {
    }

    @Override
    public void run() {
        this.eventLoopGroup = PipelineUtil.getEventLoopGroup();

        this.bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.IP_TOS, 24)
                .option(ChannelOption.TCP_NODELAY, true)
                .channelFactory(PipelineUtil.getFactory())
                //.channel(PipelineUtil.getChannel())
                .handler(PipelineUtil.getChannelInitializer(this, CloudDriver.class.getAnnotation(DriverInfo.class).packetProtocolVersion()));

        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        try {
            this.channel = bootstrap.connect(getHost(), getPort()).sync().channel();
            channel.closeFuture().sync().syncUninterruptibly();
        } catch (Exception e) {
            try {
                throw e;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    @Override
    public void sendPacket(IPacket packet, Channel channel) {
        this.networkBus.processOut(channel, packet);
    }

    @Override
    public void sendPacket(IPacket packet) {
        this.networkBus.processOut(channel, packet);
    }

    @Override
    public void registerNetworkAdapter(INetworkAdapter adapter) {
        this.networkAdapters.add(adapter);
    }

    @Override
    public void registerPacketHandler(IPacketHandler packetHandler) {
        this.packetHandlers.add(packetHandler);
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    @Override
    public void shutdown() {
        channel.close();
        channel.disconnect();
    }

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(getHost(), getPort());
    }

}
