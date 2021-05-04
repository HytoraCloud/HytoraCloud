package de.lystx.cloudsystem.library.network.connection;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.lystx.cloudsystem.library.network.connection.NetworkInstance;
import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.PacketRegistry;
import de.lystx.cloudsystem.library.network.packet.response.ResponseData;
import de.lystx.cloudsystem.library.network.packet.response.ResponseStatus;
import de.lystx.cloudsystem.library.network.packet.PacketAdapting;
import de.lystx.cloudsystem.library.network.packet.impl.PacketHandshake;
import de.lystx.cloudsystem.library.network.packet.impl.PacketRespond;
import de.lystx.cloudsystem.library.network.connection.server.NetworkServer;
import io.netty.channel.Channel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Class for handling incoming and outgoing packets
 */
public class NetworkBus {

    @Getter
    private NetworkInstance handle;

    @Getter
    private ExecutorService executors
            = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("networkbus-pool-%d").build());

    public NetworkBus(NetworkInstance handle) {
        this.handle = handle;
    }

    /**
     * Processes the packet (first step after receiving)
     *
     * @param channel The channel who sent the packet
     * @param packet  The packet which was sent
     */
    public synchronized void processIn(Channel channel, AbstractPacket packet) {
        // call request/response system
        // Also the packets request/response system
        if(channel != null) {
            handle.getLogger().info("(Packet : [" + packet.getClass().getSimpleName() + "]) (UUID : [" + packet.getUniqueId() + "]) (Stamp : [" + packet.getStamp() + "]) (Data : [" + packet.getBuf().capacity() + "]) (ProtocolID : [" + packet.getProtocolId() + "]) (ProtocolVersion : [" + packet.getProtocolVersion() + "]) (Channel : [" + packet.getChannel() + "])");
        }

        // call packets processing
        handle.callEvent(adapter -> adapter.handlePacketReceive(packet));
        PacketAdapting.getInstance().execute(packet);

        final ResponseData responseData = packet.createResponse();
        if (responseData != null) {
            packet.respond(new PacketRespond("response", responseData.getMessage(), responseData.getStatus()));
        }

        // call handler event
        if (packet instanceof PacketHandshake) {
            handle.callEvent(adapter -> adapter.handleHandshake((PacketHandshake) packet));
            handle.setAuthenticated(true);
        } else if (handle instanceof NetworkServer) {
            NetworkServer server = (NetworkServer) handle;

            if(channel != null) {
                packet.respond(new PacketRespond(ResponseStatus.FORBIDDEN));
                return;
            }
        }

        // handle callbacks
        if(handle.getCallbacks().asMap().containsKey(packet.getUniqueId())) {
            List<Consumer<AbstractPacket>> callbacks = handle.getCallbacks().getIfPresent(packet.getUniqueId());
            if(callbacks != null) {
                // found callbacks for this packet, execute them ..
                executors.execute(() -> callbacks.forEach(consumer -> consumer.accept(packet)));
            }
        }

    }

    /**
     * Processes the packet (last step before sending)
     *
     * @param channel   The channel to send the packet to
     * @param packet    The packet to be sent
     * @param callbacks Callback after receiving a respond
     */

    @SafeVarargs
    public final synchronized void processOut(Channel channel, AbstractPacket packet, Consumer<AbstractPacket>... callbacks) {
        int protocolId = PacketRegistry.getInstance().getId(packet.getClass());
        if (protocolId == -1) {
            handle.getLogger().warning("Can't send Packet because it's not registered! (id: " + protocolId + ")");
            return;
        }
        if(channel == null) {
            return;
        }

        // send time and identifier
        packet.setStamp(System.currentTimeMillis());
        if(packet.getUniqueId() == null) {
            packet.setUniqueId(UUID.nameUUIDFromBytes(("Time:" + System.nanoTime()).getBytes(Charsets.UTF_8)));
        }
        channel.writeAndFlush(packet);

        // releases the packetbuf
        try {
            packet.getBuf().release();
        }
        catch(Exception e) {
            //
        }

        // callbacks
        if(callbacks.length != 0) {
            handle.getCallbacks().put(packet.getUniqueId(), new ArrayList<>(Arrays.asList(callbacks)));
        }

        // call handler event
        handle.callEvent(adapter -> adapter.handlePacketSend(packet));

        // packet content
        String content = (packet instanceof PacketRespond ? " {" + packet.toString().split("\"payload\": ")[1] : "");
        String id = (packet.getUniqueId() + "").substring(0, 2);

        handle.getLogger().info("[Outgoing " + id + "] '" + packet.getClass().getSimpleName() + "'" + content);
    }

}
