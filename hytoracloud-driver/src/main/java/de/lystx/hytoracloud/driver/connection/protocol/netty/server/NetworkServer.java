package de.lystx.hytoracloud.driver.connection.protocol.netty.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.DriverInfo;
import de.lystx.hytoracloud.driver.connection.protocol.netty.NettyConstants;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel.INetworkChannel;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.INettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.manager.IClientManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification.ConnectionType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.*;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.manager.DefaultClientManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.handling.INetworkAdapter;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.NetworkInstance;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.PacketHandshake;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * The network server which is used to receive connections of {@link NetworkClient}s
 */
@Getter
public class NetworkServer extends NetworkInstance implements INetworkServer {

    private final ChannelGroup connectedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * The client manager
     */
    private final IClientManager clientManager;

    public NetworkServer(String host, int port, ConnectionType type) {
        super(host, port, type);

        this.clientManager = new DefaultClientManager();

        // register adapter
        this.registerNetworkAdapter(new INetworkAdapter() {
            @Override
            public void onPacketReceive(IPacket packet) {}

            @Override
            public void onHandshakeReceive(PacketHandshake handshake) {}

            @Override
            public void onPacketSend(IPacket packet) {}

            @Override
            public void onChannelActive(INetworkChannel channel) {
                connectedClients.add(channel.nettyVariant());

                channel.sendPacket(new PacketHandshake());
            }

            @Override
            public void onChannelInactive(INetworkChannel channel) {
                connectedClients.removeIf(channel1 -> channel1.remoteAddress().equals(channel.remoteAddress()) && channel.localAddress().equals(channel1.localAddress()));

                InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
                INettyClient nettyClient = clientManager.getClient(remoteAddress);

                if (nettyClient == null) {
                    System.out.println("[NetworkServer] Client shouldn't be null at disconnecting (Address: " + remoteAddress + ")." + " This can happen if the client tried to connect but wasn't able to handshake properly (Maybe he didn't have hands?)");
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
    public void bootstrap()  {

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NettyConstants.CHANNEL_SERVER)
                    .childHandler(getChannelInitializer(CloudDriver.class.getAnnotation(DriverInfo.class).packetProtocolVersion()))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            // Start the server.
            ChannelFuture f = serverBootstrap.bind(host, port).sync();
            // Wait until the server socket is closed.
            try {
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                //Closed server
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void shutdown() {
        channel.close();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    @Override
    public void sendPacket(IPacket packet) {
        for (Channel connectedClient : this.connectedClients) {
            this.sendPacket(packet, connectedClient);
        }
    }

}
