package de.lystx.hytoracloud.networking.connection;

import de.lystx.hytoracloud.driver.service.event.CloudEventHandler;
import de.lystx.hytoracloud.driver.service.event.IEventService;
import de.lystx.hytoracloud.networking.events.CloudPacketQueueEvent;
import de.lystx.hytoracloud.networking.events.CloudPacketQueryEvent;
import de.lystx.hytoracloud.networking.exceptions.NetworkGatewayOutputException;
import de.lystx.hytoracloud.networking.netty.NettyChannelInboundHandler;
import de.lystx.hytoracloud.networking.netty.codec.standard.PacketDecoder;
import de.lystx.hytoracloud.networking.netty.codec.standard.PacketEncoder;
import de.lystx.hytoracloud.networking.netty.codec.varint.Varint32FrameDecoder;
import de.lystx.hytoracloud.networking.netty.codec.varint.Varint32LengthFieldPrepender;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.manager.PacketAdapter;
import de.lystx.hytoracloud.networking.packet.manager.PacketMessenger;
import de.lystx.hytoracloud.networking.packet.impl.response.Response;
import de.lystx.hytoracloud.networking.provided.objects.NetworkEventAdapter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public abstract class NetworkExecutor<T> {

    /**
     * The channel of this connection to flush packets
     */
    protected Channel channel;

    /**
     * The EventGroup to work with
     */
    protected EventLoopGroup eventLoopGroup;

    /**
     * The host to connect to
     */
    protected final String host;

    /**
     * The port to connect to
     */
    protected final int port;

    /**
     * The networkGateway to process in and out packets
     */
    protected final NetworkGateway networkGateway;

    /**
     * The adapter to handle packets
     */
    protected final PacketAdapter packetAdapter;

    /**
     * Checks if connection has shaked hands
     */
    @Setter
    protected boolean authenticated;

    /**
     * All registered EventAdapters
     */
    private final List<NetworkEventAdapter> eventAdapters;


    public NetworkExecutor(String host, int port, IEventService eventService) {
        this.host = host;
        this.port = port;


        Logger nettyLogger = Logger.getLogger("netty");
        nettyLogger.setLevel(Level.OFF);

        nettyLogger = Logger.getLogger("io.netty");
        nettyLogger.setLevel(Level.OFF);

        this.eventAdapters = new ArrayList<>();
        this.packetAdapter = new PacketAdapter();

        if (PacketMessenger.getEventService() == null) {
            PacketMessenger.setEventService(eventService);
        }
        PacketMessenger.getEventService().registerEvent(this);

        this.networkGateway = new NetworkGateway(this);
    }

    /**
     * Sets the connection up and prepares to
     * either connect or start the connection
     *
     * @return current instance
     */
    public abstract T setup();

    /**
     * Builds the connection (sets up and starts)
     * and does this in separate thread
     * and accepts a consumer when its done
     *
     * @param consumer the consumer
     * @param exceptionConsumer the consumer to handle exception if thrown
     */
    public abstract void build(Consumer<T> consumer, Consumer<Exception> exceptionConsumer);

    /**
     * Executes a Response with a default timeOut and interval
     *
     * @param packet the packet to get the response of
     * @return response
     */
    public Response executeResponse(AbstractPacket packet) {
        return this.executeResponse(packet, 3, TimeUnit.SECONDS);
    }
    /**
     * This requests a {@link Response} from a {@link AbstractPacket}
     *
     * @param packet the packet to get the response of
     * @param timeOut the timeOut interval
     * @param timeUnit the unit to use the interval with
     * @return response
     */
    private Response executeResponse(AbstractPacket packet, int timeOut, TimeUnit timeUnit) {

        long start = System.currentTimeMillis();
        this.sendPacket(packet);

        //Calls the event
        CloudPacketQueryEvent event = new CloudPacketQueryEvent(packet);
        PacketMessenger.getEventService().callEvent(event);

        //Throws error if event was cancelled
        if (event.isCancelled()) {
            Throwable cancelReason = event.getCancelReason();

            if (cancelReason != null) {
                try {
                    throw cancelReason;
                } catch (Throwable throwable) {
                    if (throwable instanceof NetworkGatewayOutputException) {
                        throw (NetworkGatewayOutputException) throwable;
                    }
                }
            }
            return null;
        }
        //Gets the response with timeOut of 3 seconds
        Response response = event.getSupplier().get(timeOut, timeUnit);
        response.setStamp(System.currentTimeMillis() - start);
        return response;
    }


    @CloudEventHandler
    private void handleQueue(CloudPacketQueueEvent event) {
        Channel ch = event.getChannel();
        if (ch == null) {
            ch = this.channel;
        }
        if (event.getPacket() == null) {
            return;
        }
        this.networkGateway.prepareGate(ch, event.getPacket());
    }

    /**
     * Calls an event for all adapter
     *
     * @param adapterConsumer The consumer of the adapter
     */
    public synchronized void callEvent(Consumer<NetworkEventAdapter> adapterConsumer) {
        for (NetworkEventAdapter eventAdapter : eventAdapters) {
            adapterConsumer.accept(eventAdapter);
        }
    }

    /**
     * Sends a packet from this executor
     *
     * @param packet   the packet
     * @param channel  the channel
     */
    public abstract void sendPacket(AbstractPacket packet, Channel channel);

    /**
     * Sends a {@link AbstractPacket} without any consumers to accept it
     *
     * @param packet the packet to send
     */
    public abstract void sendPacket(AbstractPacket packet);

    /**
     * Registers an event adapter
     *
     * @param adapter The adapter to register
     */
    public void registerEventAdapter(NetworkEventAdapter adapter) {
        this.eventAdapters.add(adapter);
    }

    public void createPipeline(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();

        //Handler
        pipeline.addLast(new NettyChannelInboundHandler(NetworkExecutor.this));

        //VarInt frame
        pipeline.addLast(new Varint32FrameDecoder());
        pipeline.addLast(new Varint32LengthFieldPrepender());

        //Codec
        pipeline.addLast(new PacketDecoder());
        pipeline.addLast(new PacketEncoder());

    }

}
