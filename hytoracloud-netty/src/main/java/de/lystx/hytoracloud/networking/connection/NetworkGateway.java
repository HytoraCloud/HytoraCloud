package de.lystx.hytoracloud.networking.connection;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.networking.packet.impl.response.PacketRespond;
import de.lystx.hytoracloud.networking.packet.impl.response.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Getter
public class NetworkGateway {

    /**
     * The instance of this gateway (Server or Client)
     */
    private final NetworkExecutor<?> instance;

    /**
     * The executors
     */
    private final ExecutorService executors;

    public NetworkGateway(NetworkExecutor<?> instance) {
        this.instance = instance;
        this.executors = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("networkgateway-cachedThreadPool-%d").build());
    }

    /**
     * Processes the packet (first step after receiving)
     *
     * @param packet  The packet which was sent
     */
    public synchronized void openGate(AbstractPacket packet) {
        // call request/response system
        // Also the packets request/response system

        // call packets processing
        instance.callEvent(adapter -> adapter.handlePacketReceive(packet));
        instance.getPacketAdapter().execute(packet);

        Response responseData = packet.createResponse();
        if (responseData != null) {
            packet.respond(new PacketRespond(responseData.getMessage(), responseData.getStatus(), responseData.getNetworkObjects()));
        }

        // call handler event
        if (packet instanceof PacketHandshake) {
            instance.callEvent(adapter -> adapter.handleHandshake((PacketHandshake) packet));
            instance.setAuthenticated(true);
        }

    }

    /**
     * Processes the packet (last step before sending)
     *
     * @param channel   The channel to send the packet to
     * @param packet    The packet to be sent
     */

    public synchronized void prepareGate(Channel channel, AbstractPacket packet) {
        if (channel == null) {
            System.out.println("[NetworkGateway] Couldn't send " + packet.getClass().getSimpleName() + " because Channel is null!");
            return;
        }

        // send time and uniqueId
        packet.setStamp(System.currentTimeMillis());
        packet.setUniqueId(packet.getUniqueId() == null ? UUID.randomUUID() : packet.getUniqueId());

        channel.writeAndFlush(packet);

        // call handler event
        instance.callEvent(adapter -> adapter.handlePacketSend(packet));
    }

}
