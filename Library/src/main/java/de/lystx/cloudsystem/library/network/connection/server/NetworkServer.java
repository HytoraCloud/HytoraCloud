package de.lystx.cloudsystem.library.network.connection.server;

import de.lystx.cloudsystem.library.Cloud;
import de.lystx.cloudsystem.library.network.extra.util.Protocol;
import de.lystx.cloudsystem.library.network.connection.NetworkInstance;
import de.lystx.cloudsystem.library.network.connection.client.NetworkClient;
import de.lystx.cloudsystem.library.network.connection.NetworkEventAdapter;
import de.lystx.cloudsystem.library.network.extra.event.NetworkServerStateChangeEvent;
import de.lystx.cloudsystem.library.network.extra.event.QueryEvent;
import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.impl.PacketHandshake;
import de.lystx.cloudsystem.library.network.packet.impl.PacketRespond;
import de.lystx.cloudsystem.library.network.packet.response.Response;
import de.lystx.cloudsystem.library.network.packet.response.ResponseStatus;
import de.lystx.cloudsystem.library.network.extra.util.PipelineUtil;
import de.lystx.cloudsystem.library.service.event.CloudEventHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * The network server which is used to receive connections of {@link NetworkClient}s
 */
@Getter
public class NetworkServer extends NetworkInstance {

    /*
    Netty things
     */
    private final ChannelGroup connectedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private ServerBootstrap bootstrap;
    private HostWhitelist whitelist;

    private VsonObject config;

    public NetworkServer(String host, int port, VsonObject config, Logger logger) {
        super(host, port, logger);
        this.config = config;

        // list whitelist
        whitelist = new HostWhitelist(this);
        whitelist.load();

        // list hub

        // register adapter
        super.registerEventAdapter(new NetworkEventAdapter() {
            @Override
            public void handlePacketReceive(AbstractPacket packet) {
            }

            @Override
            public void handleHandshake(PacketHandshake handshake) {

            }

            @Override
            public void handlePacketSend(AbstractPacket packet) {
                //
            }

            @Override
            public void handleChannelActive(Channel channel) {
                InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();

                connectedClients.add(channel);


                PacketHandshake handshake = new PacketHandshake("security", remoteAddress);
                sendPacket(handshake, channel);

                // attempt means that the server isn't accepted yet
                getLogger().info("Client attempting to connect .. [@" + remoteAddress.getAddress().getHostAddress() + "]");
            }

            @Override
            public void handleChannelInActive(Channel channel) {
                InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
                connectedClients.remove(channel);

                getLogger().warning( "Client shouldn't be null at disconnecting (Address: " + remoteAddress + ")." +
                        " This can happen if the client tried to connect but wasn't able to handshake properly (Maybe he didn't have hands?)");
            }
        });

        Cloud.getInstance().getEventService().registerEvent(this);

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
        this.eventExecutors = PipelineUtil.getEventLoopGroup();

        this.bootstrap = new ServerBootstrap()
                .group(eventExecutors)
                .channel(PipelineUtil.getServerChannel())
                .childHandler(PipelineUtil.getChannelInitializer(this, Protocol.VERSION));
        return this;
    }


    @Override
    public <T extends NetworkInstance> void build(Consumer<T> consumer) {
        try {
            this.setup();
            this.start();
            consumer.accept((T) this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @CloudEventHandler
    private void handleQuery(QueryEvent event) {
        AbstractPacket packet = event.getToQueryPacket();

        packet.interceptRespond(abstractPacket -> {
            if(!(abstractPacket instanceof PacketRespond)){
                event.accept(new Response(ResponseStatus.FAILED));
                return;
            }
            Response response = new Response((PacketRespond)abstractPacket);
            event.accept(response);
        });
        this.networkBus.processIn(null, packet);
    }


    /**
     * Starts the server<br>
     * To inform other parts of this program, that this server is started now, it'll call a {@link NetworkServerStateChangeEvent}
     * per
     *
     * @throws Exception If something goes wrong
     */
    public void start() throws Exception {
        Cloud.getInstance().getEventService().callEvent(new NetworkServerStateChangeEvent(this, State.STARTING));

        try {
            this.channel = bootstrap.bind(getHost(), getPort()).sync().channel();

            // calls server status event
            Cloud.getInstance().getEventService().callEvent(new NetworkServerStateChangeEvent(this, State.STARTED));

            channel.closeFuture().sync().syncUninterruptibly();
        } catch (Exception e) {
            //
        }
        finally {
            if (eventExecutors != null) {
                eventExecutors.shutdownGracefully();
            }
        }
    }

    /**
     * Stops the server.<br>
     * To do so, it will close the {@link Channel} and shutdown the {@link EventLoopGroup}
     * <p>
     * It also calls the {@link NetworkServerStateChangeEvent} to trigger events that may need to listen to this
     */
    public void stop() {
        Cloud.getInstance().getEventService().callEvent(new NetworkServerStateChangeEvent(this, State.STOPPING));

        channel.close();
        eventExecutors.shutdownGracefully();

        // calls server status event
        Cloud.getInstance().getEventService().callEvent(new NetworkServerStateChangeEvent(this, State.STOPPED));
    }


    /**
     * Broadcasts given packets to all connected clients
     *
     * @param packet The packet
     */
    @Override
    public void sendPacket(AbstractPacket packet) {
        connectedClients.forEach(channel -> sendPacket(packet, channel));
    }

    /**
     * State of the server to determine in which state the server currently is
     */
    public enum State {

        STARTING,
        STARTED,
        STOPPING,
        STOPPED

    }

}
