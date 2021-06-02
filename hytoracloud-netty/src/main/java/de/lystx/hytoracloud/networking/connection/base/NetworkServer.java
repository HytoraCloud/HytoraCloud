package de.lystx.hytoracloud.networking.connection.base;

import de.lystx.hytoracloud.driver.service.event.CloudEventHandler;
import de.lystx.hytoracloud.driver.service.event.IEventService;
import de.lystx.hytoracloud.networking.provided.objects.NetworkEventAdapter;
import de.lystx.hytoracloud.networking.connection.NetworkExecutor;
import de.lystx.hytoracloud.networking.events.CloudPacketQueryEvent;
import de.lystx.hytoracloud.networking.provided.other.NettyUtil;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.networking.packet.impl.response.PacketRespond;
import de.lystx.hytoracloud.networking.packet.impl.response.Response;
import de.lystx.hytoracloud.networking.packet.impl.response.ResponseStatus;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The network server which is used to receive connections of {@link NetworkClient}s
 */
@Getter
public class NetworkServer extends NetworkExecutor<NetworkServer> {

    private final List<Channel> connectedClients = new ArrayList<>();
    private ServerBootstrap bootstrap;


    public NetworkServer(String host, int port, IEventService eventService) {
        super(host, port, eventService);

        // register adapter
        this.registerEventAdapter(new NetworkEventAdapter() {

            public void handlePacketReceive(AbstractPacket packet) {}

            public void handleHandshake(PacketHandshake handshake) {}

            public void handlePacketSend(AbstractPacket packet) {}

            @Override
            public void handleChannelActive(Channel channel) {
                connectedClients.add(channel);

                PacketHandshake handshake = new PacketHandshake();
                sendPacket(handshake, channel);

            }

            @Override
            public void handleChannelInActive(Channel channel) {
                Channel safeGet = connectedClients.stream().filter(
                        channel1 ->
                                ((InetSocketAddress) channel1.remoteAddress()).getPort() == ((InetSocketAddress) channel.remoteAddress()).getPort()
                                        &&
                                        ((InetSocketAddress) channel1.remoteAddress()).getAddress().getHostAddress().equalsIgnoreCase(
                                                ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress()
                                        )
                ).findFirst().orElse(null);
                if (safeGet != null) connectedClients.remove(safeGet); else connectedClients.remove(channel);
            }
        });
    }

    /**
     * Sets the server up, that means it initialises the event group and the {@link ServerBootstrap}<br>
     * It adds different de- and encoder to the pipeline to handle packets<br>
     * <p>
     * On Unix systems Epoll is a pretty nice thing, so if this program runs on a Unix system
     * it'll use the {@link EpollEventLoopGroup} and the {@link EpollServerSocketChannel} instead of the default ones
     */
    @Override
    public NetworkServer setup() {
        this.eventLoopGroup = NettyUtil.getEventLoopGroup();

        this.bootstrap = new ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NettyUtil.getServerChannel())

                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        createPipeline(socketChannel);
                    }
                });
        return this;
    }


    @Override
    public void build(Consumer<NetworkServer> consumer, Consumer<Exception> exceptionConsumer) {
        new Thread(() -> {
            try {
                consumer.accept(this);
                this.setup().start();
            } catch (Exception e) {
                exceptionConsumer.accept(e);
            }
        }, "network_server").start();
    }


    @CloudEventHandler
    private void handleQuery(CloudPacketQueryEvent event) {
        AbstractPacket packet = event.getToQueryPacket();

        packet.setInterception(abstractPacket -> {
            if(!(abstractPacket instanceof PacketRespond)){
                event.accept(new Response(ResponseStatus.FAILED));
                return;
            }
            Response response = new Response((PacketRespond)abstractPacket);
            event.accept(response);
        });

        this.networkGateway.openGate(packet);
    }


    /**
     * Starts the server<br>
     * To inform other parts of this program, that this server is started now, it'll call a
     * per
     *
     * @throws Exception If something goes wrong
     */
    public void start() throws Exception {

        try {
            this.channel = bootstrap.bind(this.port).sync().channel();
            // calls server status event
            channel.closeFuture().sync().syncUninterruptibly();
        } finally {
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully();
            }
        }
    }

    /**
     * Stops the server.<br>
     * To do so, it will close the {@link Channel} and shutdown the {@link EventLoopGroup}
     * <p>
     */
    public void stop() {

        channel.close();
        eventLoopGroup.shutdownGracefully();

        // calls server status event
    }


    /**
     * Broadcasts given packets to all connected clients
     *
     * @param packet The packet
     */
    @Override
    public void sendPacket(AbstractPacket packet) {
        for (Channel connectedClient : connectedClients) {
            sendPacket(packet, connectedClient);
        }
    }

    @Override
    public void sendPacket(AbstractPacket packet, Channel channel) {
        this.networkGateway.prepareGate(channel, packet);
    }

}
