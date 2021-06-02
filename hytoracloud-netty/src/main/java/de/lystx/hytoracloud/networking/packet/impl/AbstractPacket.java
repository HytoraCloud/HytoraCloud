package de.lystx.hytoracloud.networking.packet.impl;

import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.networking.events.CloudPacketQueueEvent;
import de.lystx.hytoracloud.networking.packet.PacketBuffer;
import de.lystx.hytoracloud.networking.packet.manager.PacketMessenger;
import de.lystx.hytoracloud.networking.provided.objects.NetworkObject;
import de.lystx.hytoracloud.networking.provided.other.NettyUtil;
import de.lystx.hytoracloud.networking.packet.impl.response.PacketRespond;
import de.lystx.hytoracloud.networking.packet.impl.response.Response;
import de.lystx.hytoracloud.networking.packet.impl.response.ResponseStatus;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Consumer;

/**
 * Abstract class of a netty packet which can be sent across the network
 */
@Getter
@NoArgsConstructor
public abstract class AbstractPacket implements Serializable {

    /**
     * The unique query id (to determine request-response system)
     */
    @Setter
    protected UUID uniqueId;

    /**
     * The timestamp of the time of the sending
     */
    @Setter
    protected long stamp = -1;

    /**
     * The channel of the packet being
     */
    @Setter
    protected Channel channel;

    /**
     * The interception (for packet simulation)
     */
    @Setter
    protected Consumer<AbstractPacket> interception;

    /**
     * Converts given bytebuffer into this packets
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong
     */
    public abstract void read(PacketBuffer buf) throws IOException;

    /**
     * Converts this packets into given bytebuffer
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong
     */
    public abstract void write(PacketBuffer buf) throws IOException;

    /**
     * Respond to the packets
     *
     * @param packet    The packets to send as respond
     * @param callbacks The callbacks?
     */
    @SafeVarargs
    public final void respond(AbstractPacket packet, Consumer<AbstractPacket>... callbacks) {
        packet.setUniqueId(this.uniqueId);

        if (interception != null) {
            interception.accept(packet);
            return;
        }
        PacketMessenger.getEventService().callEvent(new CloudPacketQueueEvent(this.channel, packet, callbacks));
    }


    public void respond(ResponseStatus status) {
        this.respond(new PacketRespond("{}", status));
    }

    public void respond(ResponseStatus status, String message) {
        this.respond(new PacketRespond(message, status));
    }

    public void respond(ResponseStatus status, JsonBuilder builder) {
        this.respond(status, builder.toString());
    }

    public void respond(JsonBuilder builder) {
        this.respond(builder.toString());
    }

    public void respond(String message) {
        this.respond(ResponseStatus.OK, message);
    }

    public void respond(ResponseStatus status, NetworkObject... networkObjects) {
        this.respond(new PacketRespond("{}", status, Arrays.asList(networkObjects)));
    }

    public void respond(Object... objects) {
        JsonBuilder jsonBuilder = new JsonBuilder();
        for (int i = 0; i < objects.length; i++) {
            jsonBuilder.append(String.valueOf(i), objects[i]);
        }
        this.respond(jsonBuilder.toString());
    }

    /**
     * Copies this packet
     *
     * @param <T> .
     * @return .
     */
    @SneakyThrows
    public <T extends AbstractPacket> T deepCopy() {
        AbstractPacket packet = NettyUtil.getInstance(getClass());
        if (packet == null) {
            return (T) this;
        }

        Field[] fields = NettyUtil.getFieldsNonStatic(packet.getClass()).toArray(new Field[]{});

        for (int i = 0; i < fields.length; i++) {
            Field current = fields[i];

            if(!Modifier.isStatic(current.getModifiers())) {

                Field fieldFromId = NettyUtil.getFieldFromId(i, packet.getClass());
                fieldFromId.setAccessible(true);

                Field field = NettyUtil.getFieldFromId(i, packet.getClass());
                try {
                    field.setAccessible(true);
                    field.set(packet, field.get(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        packet.setUniqueId(null);
        packet.setStamp(0L);
        return (T) packet;
    }



    public Response createResponse() {
        return null;
    }

}
