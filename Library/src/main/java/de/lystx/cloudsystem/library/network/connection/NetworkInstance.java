package de.lystx.cloudsystem.library.network.connection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.lystx.cloudsystem.library.Cloud;
import de.lystx.cloudsystem.library.network.extra.event.QueryEvent;
import de.lystx.cloudsystem.library.network.extra.exception.NettyInputException;
import de.lystx.cloudsystem.library.network.extra.exception.NettyOutputException;
import de.lystx.cloudsystem.library.network.extra.event.PacketQueueEvent;
import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.impl.PacketHandshake;
import de.lystx.cloudsystem.library.network.packet.response.Response;
import de.lystx.cloudsystem.library.service.event.CloudEventHandler;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Getter
public abstract class NetworkInstance {


    protected Channel channel;
    protected EventLoopGroup eventExecutors;

    private final Logger logger;
    private final String host;
    private final int port;

    protected final NetworkBus networkBus;

    /**
     * The callback system
     */
    private final Cache<UUID, List<Consumer<AbstractPacket>>> callbacks = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();


    /**
     * Checks if connection has shaked hands
     */
    @Setter
    protected boolean authenticated;

    private final List<NetworkEventAdapter> eventAdapters;

    public NetworkInstance(String host, int port, Logger logger) {
        this.host = host;
        this.port = port;
        this.logger = logger;

        this.eventAdapters = new ArrayList<>();

        Cloud.getInstance().getEventService().registerEvent(this);

        this.networkBus = new NetworkBus(this);
    }

    public abstract <T extends NetworkInstance> T setup();

    public abstract <T extends NetworkInstance> void build(Consumer<T> consumer);


    public Response execute(AbstractPacket packet) throws NettyInputException {

        QueryEvent event = new QueryEvent(packet);
        Cloud.getInstance().getEventService().callEvent(event);

        if (event.isCancelled()) {
            if (event.getCancelReason() != null) {
                try {
                    throw event.getCancelReason();
                } catch (Throwable throwable) {
                    if (throwable instanceof NettyOutputException) {
                        throw (NettyOutputException) throwable;
                    }
                }
            }
            return null;
        }
        return event.getSupplier().get(3, TimeUnit.SECONDS);
    }


    @CloudEventHandler
    private void handleQueue(PacketQueueEvent event) {
        Channel ch = event.getChannel();
        if (ch == null) {
            ch = this.channel;
        }
        if (event.getPacket() == null) {
            return;
        }
        this.networkBus.processOut(ch, event.getPacket(), event.getCallbacks());
    }

    /**
     * Calls an event for all adapter
     *
     * @param adapterConsumer The consumer of the adapter
     */
    public synchronized void callEvent(Consumer<NetworkEventAdapter> adapterConsumer) {
        eventAdapters.forEach(adapterConsumer);
    }

    /**
     * Sends a packet
     *
     * @param packet    The packets
     * @param channel   The channels
     * @param consumers The consumers of the callback
     */
    @SafeVarargs
    public final void sendPacket(AbstractPacket packet, Channel channel, Consumer<AbstractPacket>... consumers) {
        this.networkBus.processOut(channel, packet, consumers);
    }

    public void sendPacket(AbstractPacket packet) {
        this.sendPacket(packet, packet1 -> {});
    }

    public void sendPacket(AbstractPacket packet, Consumer<AbstractPacket> consumers) {
        this.sendPacket(packet, channel, consumers);
    }

    public void flushPacket(AbstractPacket packet, Consumer<Response> consumer) {
        this.sendPacket(packet);
        try {
            Response execute = this.execute(packet);
            consumer.accept(execute);
        } catch (NettyInputException e) {
            e.printStackTrace();
        }
    }

    /*
      =========================================

      Different Packet Event Adapters

      ===========================================
     */

    /**
     * Registers an event adapter
     *
     * @param adapter The adapter to register
     */
    public void registerEventAdapter(NetworkEventAdapter adapter) {
        this.eventAdapters.add(adapter);
    }

    public void registerChannelActiveAdapter(Consumer<Channel> consumer) {
        this.registerEventAdapter(new NetworkEventAdapter() {
            @Override
            public void handlePacketReceive(AbstractPacket packet) {}
            @Override
            public void handleHandshake(PacketHandshake handshake) {}
            @Override
            public void handlePacketSend(AbstractPacket packet) {}
            @Override
            public void handleChannelActive(Channel channel) {
                consumer.accept(channel);
            }
            @Override
            public void handleChannelInActive(Channel channel) {}
        });
    }

    public void registerChannelInActiveAdapter(Consumer<Channel> consumer) {
        this.registerEventAdapter(new NetworkEventAdapter() {
            @Override
            public void handlePacketReceive(AbstractPacket packet) {}
            @Override
            public void handleHandshake(PacketHandshake handshake) {}
            @Override
            public void handlePacketSend(AbstractPacket packet) {}
            @Override
            public void handleChannelActive(Channel channel) {}
            @Override
            public void handleChannelInActive(Channel channel) {
                consumer.accept(channel);
            }
        });
    }

    public void registerHandshakeAdapter(Consumer<PacketHandshake> consumer) {
        this.registerEventAdapter(new NetworkEventAdapter() {
            @Override
            public void handlePacketReceive(AbstractPacket packet) {}
            @Override
            public void handleHandshake(PacketHandshake handshake) {
                consumer.accept(handshake);
            }
            @Override
            public void handlePacketSend(AbstractPacket packet) {}
            @Override
            public void handleChannelActive(Channel channel) {}
            @Override
            public void handleChannelInActive(Channel channel) {}
        });
    }

    public void registerPacketSendAdapter(Consumer<AbstractPacket> consumer) {
        this.registerEventAdapter(new NetworkEventAdapter() {
            @Override
            public void handlePacketReceive(AbstractPacket packet) {}
            @Override
            public void handleHandshake(PacketHandshake handshake) {}
            @Override
            public void handlePacketSend(AbstractPacket packet) {
                consumer.accept(packet);
            }
            @Override
            public void handleChannelActive(Channel channel) {}
            @Override
            public void handleChannelInActive(Channel channel) {}
        });
    }

}
