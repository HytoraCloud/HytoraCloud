package de.lystx.hytoracloud.driver.connection.protocol.netty.global;

import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.bus.INetworkBus;
import de.lystx.hytoracloud.driver.connection.protocol.netty.NettyConstants;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.DefaultRequestManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.IRequestManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel.INetworkChannel;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel.NetworkChannelObject;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.handling.IChannelHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.PacketChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification.ConnectionType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.handling.INetworkAdapter;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.bus.NetworkBusObject;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.codec.PacketDecoder;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.codec.PacketEncoder;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.codec.protocol.FrameDecoder;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.codec.protocol.LengthFieldPrepender;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import io.netty.channel.*;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public abstract class NetworkInstance implements INetworkConnection {


    /**
     * The host of the server
     */
    protected final String host;

    /**
     * The port of the server
     */
    protected final int port;

    /**
     * The event handler from netty
     */
    protected final EventLoopGroup bossGroup, workerGroup;

    /**
     * The network bus for handling incoming and outgoing connections
     */
    protected final INetworkBus networkBus;

    /**
     * The netty channel of the instance
     */
    protected Channel channel;

    /**
     * All registered adapters
     */
    protected final List<INetworkAdapter> networkAdapters;

    /**
     * All registered packet handlers
     */
    protected final List<IPacketHandler> packetHandlers;

    /**
     * All registered channelHandlers
     */
    protected final Map<String, List<IChannelHandler>> channelHandlers;

    /**
     * The request manager
     */
    protected final IRequestManager requestManager;

    /**
     * The type
     */
    protected final ConnectionType type;


    public NetworkInstance(String host, int port, ConnectionType type) {
        this.host = host;
        this.port = port;
        this.type = type;

        this.networkBus = new NetworkBusObject(this);
        this.packetHandlers = new LinkedList<>();
        this.channelHandlers = new HashMap<>();
        this.networkAdapters = new LinkedList<>();
        this.requestManager = new DefaultRequestManager(this);

        this.bossGroup = NettyConstants.EVENT_LOOP_GROUP;
        this.workerGroup = NettyConstants.WORKER_LOOP_GROUP;
    }


    public INetworkChannel getChannel() {
        return new NetworkChannelObject(this, channel);
    }

    public void setChannel(INetworkChannel channel) {
        this.channel = channel.nettyVariant();
    }

    protected ChannelInitializer<Channel> getChannelInitializer(int protocolVersion) {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                // Codec
                pipeline.addLast(NettyConstants.INITIALIZER_FRAME_DECODER, new FrameDecoder());

                // Packet decoder
                PacketDecoder decoder = new PacketDecoder(NetworkInstance.this);
                decoder.setProtocolVersion(protocolVersion);
                pipeline.addLast(NettyConstants.INITIALIZER_PACKET_DECODER, decoder);

                pipeline.addLast(NettyConstants.INITIALIZER_FRAME_PREPENDER, new LengthFieldPrepender());

                // Packet encoder
                PacketEncoder encoder = new PacketEncoder();
                encoder.setProtocolVersion(protocolVersion);
                pipeline.addLast(NettyConstants.INITIALIZER_PACKET_ENCODER, encoder);

                // Handler
                pipeline.addLast(NettyConstants.INITIALIZER_BOSS_HANDLER, new SimpleChannelInboundHandler<NettyPacket>() {

                    @Override
                    public void channelRead0(ChannelHandlerContext ctx, NettyPacket packet) throws Exception {
                        getNetworkBus().processIn(ctx.channel(), packet);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        if (cause.getMessage() == null && !(cause instanceof IOException)) {
                            cause.printStackTrace();
                        }
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        // WHEN A CHANNEL CONNECTS TO THE SERVER
                        // OR WHEN THE CLIENT CONNECTS TO THE SERVER

                        // call handler event
                        for (INetworkAdapter adapter : getNetworkAdapters()) {
                            adapter.onChannelActive(new NetworkChannelObject(NetworkInstance.this, ctx.channel()));
                        }
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        // WHEN A CHANNEL DISCONNECTS FROM THE SERVER
                        // OR WHEN THE CLIENT DISCONNECTS FROM THE SERVER

                        // call handler event
                        for (INetworkAdapter adapter : getNetworkAdapters()) {
                            adapter.onChannelInactive(new NetworkChannelObject(NetworkInstance.this, ctx.channel()));
                        }
                    }
                });
            }
        };
    }


    @Override
    public void registerChannelHandler(String channel, IChannelHandler channelHandler) {
        List<IChannelHandler> channelHandlers = this.channelHandlers.get(channel);
        if (channelHandlers == null) {
            channelHandlers = new LinkedList<>();
        }
        channelHandlers.add(channelHandler);
        this.channelHandlers.put(channel, channelHandlers);
    }

    public List<IChannelHandler> getChannelHandlers() {
        List<IChannelHandler> channelHandlers = new LinkedList<>();
        for (List<IChannelHandler> value : this.channelHandlers.values()) {
            channelHandlers.addAll(value);
        }
        return channelHandlers;
    }

    @Override
    public List<IChannelHandler> getChannelHandlers(String channel) {
        return this.channelHandlers.getOrDefault(channel, new LinkedList<>());
    }

    @Override
    public void sendPacket(IPacket packet, Channel channel) {
        this.networkBus.processOut(channel, packet);
    }

    @Override
    public void sendPacket(IPacket packet) {
        this.sendPacket(packet, channel);
    }

    @Override
    public void registerNetworkAdapter(INetworkAdapter adapter) {
        this.networkAdapters.add(adapter);
    }

    @Override
    public void unregisterPacketHandler(IPacketHandler packetHandler) {
        this.packetHandlers.remove(packetHandler);
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
    public void sendChannelMessage(IChannelMessage message) {
        this.sendPacket(new PacketChannelMessage(message));
    }

    @Override
    public INetworkConnection parent() {
        return this;
    }

}
