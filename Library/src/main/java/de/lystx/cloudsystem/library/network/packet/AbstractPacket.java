package de.lystx.cloudsystem.library.network.packet;

import de.lystx.cloudsystem.library.Cloud;
import de.lystx.cloudsystem.library.network.extra.util.Protocol;
import de.lystx.cloudsystem.library.network.packet.response.ResponseData;
import de.lystx.cloudsystem.library.network.packet.response.ResponseStatus;
import de.lystx.cloudsystem.library.network.extra.util.ReflectionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import de.lystx.cloudsystem.library.network.extra.event.PacketQueueEvent;
import de.lystx.cloudsystem.library.network.packet.impl.PacketRespond;

import java.io.IOException;
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
public abstract class AbstractPacket {


    /**
     * The regex of one part of the json representation<br>
     *
     * @see #toString()
     */
    public static final String JSON_PART_REGEX = "\"[a-zA-Z]*\": (\"[^\"]*\"|\\[[^}]*]|[^\",}]*)";

    /**
     * The version of the {@link Protocol}
     */
    @Setter
    protected int protocolVersion = 0;

    /**
     * The id of the packet inside the protocol<br>
     * e.g.:  would be 0
     */
    @Setter
    protected int protocolId = -1;

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
     * The byte buf (originally the raw form of the packet)
     */
    @Setter
    protected ByteBuf buf;

    /**
     * The interception (for packet simulation)
     */
    protected Consumer<AbstractPacket> interception;

    /**
     * If the packet has already been responded
     */
    private boolean responded = false;

    public void interceptRespond(Consumer<AbstractPacket> interception) {
        this.interception = interception;
    }

    /**
     * Converts given bytebuffer into this packets
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong lel
     */
    public abstract void read(PacketBuffer buf) throws IOException;

    /**
     * Converts this packets into given bytebuffer
     *
     * @param buf The byte buffer
     * @throws IOException If something goes wrong lel
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
        if(!responded) responded = true;
        packet.setUniqueId(getUniqueId());

        if(interception != null) {
            interception.accept(packet);
            return;
        }
        Cloud.getInstance().getEventService().callEvent(new PacketQueueEvent(getChannel(), packet, callbacks));
    }


    public final void respond(ResponseStatus status) {
        this.respond(new PacketRespond(getClass().getSimpleName().toLowerCase(), "", status));
    }

    public final void respond(ResponseStatus status, String message) {
        this.respond(new PacketRespond(getClass().getSimpleName().toLowerCase(), message, status));
    }

    /**
     * Copies this packet
     *
     * @param <T> .
     * @return .
     */
    public <T extends AbstractPacket> T deepCopy() {
        Object instance = ReflectionUtil.getInstance(getClass());
        if(instance == null) return (T) this;

        Field[] fields = ReflectionUtil.getFieldsNonStatic(instance.getClass()).toArray(new Field[]{});
        for(int i = 0; i < fields.length; i++) {
            Field current = fields[i];

            if(!Modifier.isStatic(current.getModifiers())) {
                ReflectionUtil.setFieldObject(i, instance, ReflectionUtil.getFieldObject(i, this));
            }
        }

        ((AbstractPacket) instance).setUniqueId(null);
        ((AbstractPacket) instance).setStamp(0L);
        return (T)(instance == null ? this : instance);
    }


    /**
     * Get the address of the channel
     *
     * @return The address
     */
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) getChannel().remoteAddress();
    }

    public ResponseData createResponse() {
        return null;
    }

    /**
     * Overridden method toString which converts the packets's content into a json string
     *
     * @return The json formatted string
     */
    @Override
    public String toString() {

        VsonObject vsonObject = new VsonObject();

        vsonObject.append("protocolVersion", protocolVersion);
        vsonObject.append("protocolId", protocolId);
        vsonObject.append("uniqueId", uniqueId);
        vsonObject.append("stamp", stamp);
        vsonObject.append("data",
                new VsonObject()
                    .append("name", getClass().getSimpleName())
                    .append("package", getClass().getPackage() == null ? "Default Package   " : getClass().getPackage().getName())
                    .append("constructors", getClass().getConstructors().length)
        );

        return vsonObject.toString(FileFormat.JSON);
    }

}
