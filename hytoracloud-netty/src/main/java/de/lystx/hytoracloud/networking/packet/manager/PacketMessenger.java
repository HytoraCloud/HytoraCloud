package de.lystx.hytoracloud.networking.packet.manager;

import de.lystx.hytoracloud.driver.service.event.IEventService;
import de.lystx.hytoracloud.networking.events.CloudPacketQueueEvent;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.provided.other.NettyUtil;
import de.lystx.hytoracloud.networking.provided.supplier.LazySupplier;
import de.lystx.hytoracloud.networking.packet.impl.response.PacketRespond;
import de.lystx.hytoracloud.networking.packet.impl.response.Response;
import de.lystx.hytoracloud.networking.packet.impl.response.ResponseScope;
import io.netty.channel.Channel;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * The packetMessenger is for sending {@link AbstractPacket}'s to a target
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PacketMessenger {

    /**
     * The class of the response scope (e.g. {@link PacketRespond})
     */
    private Class<? extends AbstractPacket> responseScopeClass = AbstractPacket.class;

    /**
     * The responseScope enum to override {@link #responseScopeClass}
     */
    private ResponseScope responseScope;

    /**
     * The targets to send the packet to
     */
    private final List<Channel> target = new ArrayList<>();

    /**
     * Sync packet sending (recommended is async)
     */
    private boolean sync = false;

    /**
     * The eventManager to call query
     */
    @Setter @Getter
    private static IEventService eventService;


    /**
     * Simply sends packet without awaiting a response.
     *
     * @param packet   The packet to be sent
     * @param channels The channels to the packet be sent to
     * @see #transfer(AbstractPacket, Class, Consumer[])
     */
    public static <P extends AbstractPacket> void message(P packet, Channel... channels) {
        PacketMessenger.create().target(channels).send(packet);
    }

    public static <P extends AbstractPacket> void message(P packet) {
        PacketMessenger.create().send(packet);
    }

    /**
     * Similar to {@link #message(AbstractPacket, Channel...)} but with awaiting a response
     *  <b>ASYNC METHOD</b>
     *
     * @param packet   The packet to be sent
     * @param consumer The consumer if a response comes back
     * @param channels The channels to be receiving the packets
     */
    public static <P extends AbstractPacket> void message(P packet, Consumer<Response> consumer, Channel... channels) {
        PacketMessenger.create().target(channels).send(packet, consumer);
    }

    public static <P extends AbstractPacket> void message(P packet, Consumer<Response> consumer) {
        PacketMessenger.create().send(packet, consumer);
    }

    /**
     * Sends a packet to the currently connected client with awaiting a response. Uses this class to achieve that<br>
     * Similar to {@link #transfer(AbstractPacket, ResponseScope)} but with consumer.<br>
     *     <b>ASYNC METHOD</b>
     *
     * @param packet             The packet
     * @param responseScopeClass The scope class
     * @param <R>                The type
     */@SafeVarargs
    public static <R, P extends AbstractPacket> void transfer(P packet, Class<? extends AbstractPacket> responseScopeClass, Consumer<R>... consumer) {
        PacketMessenger.create().responseScope(responseScopeClass).send(packet, consumer);
    }
    @SafeVarargs
    public static <R, P extends AbstractPacket> void transfer(P packet, ResponseScope scope, Consumer<R>... consumer) {
        PacketMessenger.create().responseScope(scope).send(packet, consumer);
    }
    @SafeVarargs
    public static <R, P extends AbstractPacket> void transfer(P packet, Consumer<R>... consumer) {
        PacketMessenger.create().send(packet, consumer);
    }

    @SafeVarargs
    public static <P extends AbstractPacket> void transferToResponse(P packet, Consumer<Response>... consumer) {
        PacketMessenger.create().responseScope(ResponseScope.RESPONSE).send(packet, consumer);
    }

    /**
     * Sends a packet to the currently connected client with awaiting a response. Uses this class to achieve that<br>
     *     <b>SYNC METHOD</b>
     *
     * @param packet             The packet
     * @param responseScopeClass The scope class
     * @param <R>                The type
     * @return The respond
     */
    public static <R> R transfer(AbstractPacket packet, Class<? extends AbstractPacket> responseScopeClass) {
        return PacketMessenger.create().responseScope(responseScopeClass).sync().send(packet);
    }
    public static <R> R transfer(AbstractPacket packet, ResponseScope scope) {
        return PacketMessenger.create().responseScope(scope).sync().send(packet);
    }

    public static <R> R transfer(AbstractPacket packet) {
        return PacketMessenger.create().sync().send(packet);
    }

    public static Response transferToResponse(AbstractPacket packet) {
        return PacketMessenger.create().responseScope(ResponseScope.RESPONSE).sync().send(packet);
    }

    /**
     * Creates a new PacketMessenger object
     * This is the method to initialize the object
     * IMPORTANT.
     *
     * @return The object
     */
    public static PacketMessenger create() {
        return new PacketMessenger();
    }

    /**
     * Sets the response scope that means which type should be returned as response
     *
     * @param scope The scope as enum
     * @return This
     */
    public PacketMessenger responseScope(ResponseScope scope) {
        this.responseScope = scope;
        if (responseScope.getWrappedClass() != null) {
            this.responseScopeClass = scope.getWrappedClass();
        }
        return this;
    }

    public PacketMessenger responseScope(Class<? extends AbstractPacket> clazz) {
        this.responseScopeClass = clazz;
        return this;
    }

    /**
     * Sets the target of this packet to send to
     *
     * @param channels The channels
     * @return This
     */
    public PacketMessenger target(Channel... channels) {
        this.target.addAll(Arrays.asList(channels));
        return this;
    }

    /**
     * Sets the given {@link IEventService} to call all events
     *
     * @param eventService the service
     * @return This
     */
    public PacketMessenger eventService(IEventService eventService) {
        this.eventService = eventService;
        return this;
    }


    /**
     * The packet will be sent sync (means that the {@link #send(AbstractPacket, Consumer[])} method will return something,
     * no need for a consumer here
     *
     * @return This
     */
    public PacketMessenger sync() {
        sync = !sync;
        return this;
    }


    /**
     * Converts {@code abstractPacket} to a response for given consumers
     *
     * @param abstractPacket The abstractPacket (response from smth else)
     * @param consumers      The consumers
     */
    private void convertResponse(AbstractPacket abstractPacket, LazySupplier supplier, Consumer... consumers) {
        if(consumers == null) consumers = new Consumer[0];
        if(responseScope != null) {
            if(responseScope == ResponseScope.DEFAULT) {
                for(Consumer c : consumers) {
                    c.accept(abstractPacket);
                }
                return;
            }

            // response needs to be PacketRespond
            if(responseScope == ResponseScope.RESPONSE || responseScope == ResponseScope.RESPOND) {
                boolean b = ResponseScope.RESPOND.getWrappedClass().isAssignableFrom(abstractPacket.getClass());

                if(responseScope == ResponseScope.RESPONSE) {
                    Response response = new Response((PacketRespond) abstractPacket);

                    for(Consumer c : consumers) {
                        if(b) c.accept(response);
                        else c.accept(null);
                    }

                    if(supplier != null) {
                        supplier.accept(response);
                    }
                }
                else {
                    for(Consumer c : consumers) {
                        if(b) c.accept(abstractPacket);
                        else c.accept(null);
                    }
                }
            }
        }
        else {
            boolean b = responseScopeClass == null || responseScopeClass.isAssignableFrom(abstractPacket.getClass());

            // accept the packet if the scope is correct
            try {
                for(Consumer c : consumers) {
                    if(b) c.accept(abstractPacket);
                    else c.accept(null);
                }
            }
            catch(ClassCastException ex) {
                // consumer wanted to accept something different
                // trying response type if abstractPacket is packetRespond
                if(!PacketRespond.class.isAssignableFrom(abstractPacket.getClass())) {
                    return;
                }
                Response response = new Response((PacketRespond) abstractPacket);

                try {
                    for(Consumer c : consumers) {
                        if(b) c.accept(response);
                        else c.accept(null);
                    }
                } catch(ClassCastException ex2) {
                    // givin' up ..
                }
            }
        }
    }

    /**
     * Sends the packet with inherited values. That means that all values passed to this class
     * before will be used to send the packet. Methods to pass down the values:<br>
     * {@link #responseScope(ResponseScope)}, {@link #target(Channel...)} and {@link #sync()}<br>
     * If you want to run the packet processing sync you have to use {@link #sync()} and you have to ignore {@code consumers} as well and just let
     * the method be at it is. If you want to do the other way 'round just don't use {@link #sync()} (either way no consumers needed).
     *
     * @param packet    The packet
     * @param consumers The consumers
     * @param <R>       The type of response
     * @return The response or null
     */
    @SafeVarargs
    @SneakyThrows
    public final synchronized <R> R send(AbstractPacket packet, Consumer<R>... consumers) {

        if (eventService == null) {
            throw new IllegalAccessException("Can't send Packet " + packet.getClass().getSimpleName() + " because IEventService was not set");
        }
        // automatically copying packet for forwarding
        if(packet.getStamp() != -1) {
            packet = packet.deepCopy();
        }

        boolean sync = isSync() && consumers.length == 0;
        if(sync) {
            NettyUtil.checkAsyncTask();
        }

        // sync handling
        LazySupplier<R> supplier = new LazySupplier<>();
        Consumer<AbstractPacket> packetConsumer = abstractPacket -> {
            try {
                convertResponse(abstractPacket, sync ? supplier : null, consumers);
            }
            catch(Exception e) {
                // error while converting response
                System.err.println("Error while receiving response inside PacketMessenger: ");
                e.printStackTrace();
            }

            if(sync && supplier.isEmpty()) {
                try {
                    supplier.accept((R) abstractPacket);
                }
                catch(Exception e) {
                    // error while casting packet to R
                    supplier.accept(null);
                }
            }
        };

        if (target.size() == 0) {
            eventService.callEvent(new CloudPacketQueueEvent(null, packet, packetConsumer));
        } else {
            for(Channel channel : target) {
                eventService.callEvent(new CloudPacketQueueEvent(channel, packet, packetConsumer));
            }
        }

        if(sync) {
            return supplier.get();
        }
        return null;
    }

}
