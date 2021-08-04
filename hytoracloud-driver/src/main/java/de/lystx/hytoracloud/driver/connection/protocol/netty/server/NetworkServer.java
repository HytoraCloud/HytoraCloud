package de.lystx.hytoracloud.driver.connection.protocol.netty.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.DriverInfo;
import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkBus;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.INettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.manager.IClientManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.NetworkBusObject;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.*;
import de.lystx.hytoracloud.driver.connection.protocol.netty.manager.DefaultClientManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.INetworkAdapter;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.driver.utils.other.PipelineUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * The network server which is used to receive connections of {@link NetworkClient}s
 */
@Getter
public class NetworkServer extends Thread implements INetworkServer {


    private final ChannelGroup connectedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * The client manager
     */
    private final IClientManager clientManager;

    /**
     * The port of the server
     */
    private final int port;

    /**
     * The host of the server
     */
    private final String host;

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
    private Channel channel;

    /**
     * All registered adapters
     */
    private final List<INetworkAdapter> networkAdapters;

    /**
     * All registered adapters
     */
    private final List<IPacketHandler> packetHandlers;

    public NetworkServer(String host, int port) {

        this.host = host;
        this.port = port;

        this.networkBus = new NetworkBusObject(this);
        this.clientManager = new DefaultClientManager();
        this.packetHandlers = new LinkedList<>();
        this.networkAdapters = new LinkedList<>();


        // register adapter
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
                connectedClients.add(channel);
            }

            @Override
            public void onChannelInactive(Channel channel) {
                connectedClients.remove(channel);

                InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
                INettyClient nettyClient = clientManager.getClient(remoteAddress);

                if (nettyClient == null) {
                    System.out.println("Client shouldn't be null at disconnecting (Address: " + remoteAddress + ")." + " This can happen if the client tried to connect but wasn't able to handshake properly (Maybe he didn't have hands?)");
                    return;
                }
                clientManager.unregisterClient(nettyClient);

            }
        });
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void run() {
        this.eventLoopGroup = PipelineUtil.getEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(eventLoopGroup)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.IP_TOS, 24)
                .childOption(ChannelOption.AUTO_READ, true)
                .channelFactory(PipelineUtil.getFactoryServer())
                //.channel(PipelineUtil.getServerChannel())
                .childHandler(PipelineUtil.getChannelInitializer(this, CloudDriver.class.getAnnotation(DriverInfo.class).packetProtocolVersion()));

        try {
            this.channel = bootstrap.bind(getHost(), getPort()).sync().channel();

            channel.closeFuture().sync().syncUninterruptibly();
        } catch(Exception e) {
            //
        } finally {
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully();
            }
        }
    }



    @Override
    public void bootstrap() throws Exception {
        this.start();
    }

    @Override
    public void shutdown() {
        channel.close();
        eventLoopGroup.shutdownGracefully();

    }

    @Override
    public void sendPacket(IPacket packet) {
        for (Channel connectedClient : this.connectedClients) {
            this.sendPacket(packet, connectedClient);
        }
    }


    @Override
    public void registerPacketHandler(IPacketHandler packetHandler) {
        this.packetHandlers.add(packetHandler);
    }

    @Override
    public void sendPacket(IPacket packet, Channel channel) {
        this.networkBus.processOut(channel, packet);
    }

    @Override
    public void registerNetworkAdapter(INetworkAdapter adapter) {
        this.networkAdapters.add(adapter);
    }

}
