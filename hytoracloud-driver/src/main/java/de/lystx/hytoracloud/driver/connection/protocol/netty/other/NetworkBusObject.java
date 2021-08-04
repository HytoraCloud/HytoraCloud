package de.lystx.hytoracloud.driver.connection.protocol.netty.other;

import com.google.common.base.Charsets;
import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkBus;
import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.channel.NetworkChannelObject;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.INettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.messenger.IChannelHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.messenger.PacketChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.forwarding.ForwardingPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.PacketClientCredentials;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.INetworkServer;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Class for handling incoming and outgoing packets
 */
@RequiredArgsConstructor
public class NetworkBusObject implements INetworkBus {

    private boolean logging = false;

    @Override
    public void enableLogging() {
        this.logging = true;
    }

    @Override
    public void disableLogging() {
        this.logging = false;
    }

    @Getter
    private final INetworkConnection networkConnection;

    /**
     * Processes the packet (first step after receiving)
     *
     * @param channel The channel who sent the packet
     * @param packet  The packet which was sent
     */
    public synchronized void processIn(Channel channel, IPacket packet) {

        if (channel != null && logging) {
            String id = (packet.getUniqueId() + "").substring(0, 2);
            System.out.println("[Incoming " + id + "] '" + packet.getClass().getName() + "'");
        }

        // call handler event
        if(packet instanceof PacketHandshake) {
            for (INetworkAdapter adapter : this.networkConnection.getNetworkAdapters()) {
                adapter.onHandshakeReceive((PacketHandshake) packet);
            }
        }

        // call handler event
        for (INetworkAdapter adapter : this.networkConnection.getNetworkAdapters()) {
            adapter.onPacketReceive(packet);
        }

        if (packet instanceof ForwardingPacket) {
            ForwardingPacket<?> forwardingPacket = (ForwardingPacket<?>)packet;
            if (forwardingPacket.isForward()) {
                this.processOut(channel, (IPacket) forwardingPacket.forward(false));
            }
        }

        if (packet instanceof PacketHandshake) {
            return;
        }
        if (packet instanceof PacketChannelMessage) {
            PacketChannelMessage packetChannelMessage =(PacketChannelMessage) packet;
            IChannelMessage channelMessage = packetChannelMessage.getChannelMessage();
            List<IChannelHandler> channelHandlers = networkConnection.getChannelHandlers(channelMessage.getChannel());
            for (IChannelHandler channelHandler : channelHandlers) {
                channelHandler.handle(packetChannelMessage, "", channelMessage);
            }
            return;
        }
        if (packet instanceof PacketClientCredentials && networkConnection instanceof INetworkServer) {
            PacketClientCredentials clientCredentials = (PacketClientCredentials)packet;
            INettyClient nettyClient = clientCredentials.getNetworkClient();

            if (nettyClient == null) {
                System.out.println("[NettyServer] Exception when reading ClientCredentials: NettyClient was null (Packet did not perform well)");
                return;
            }

            if (packet.getChannel() == null) {
                System.out.println("[NettyServer] Exception when reading ClientCredentials: Channel of packet was null!");
                return;
            }
            nettyClient.setChannel(new NetworkChannelObject(networkConnection, packet.getChannel()));
            ((INetworkServer)networkConnection).getClientManager().registerClient(nettyClient);

            return;
        }
        // call packets processing
        for (IPacketHandler packetHandler : this.networkConnection.getPacketHandlers()) {
            packetHandler.handle(packet);
        }

    }

    /**
     * Processes the packet (last step before sending)
     *
     * @param channel   The channel to send the packet to
     * @param p    The packet to be sent
     */
    public synchronized void processOut(Channel channel, IPacket p) {
        NettyPacket packet = (NettyPacket) p;
        if(channel == null) {
            return;
        }

        // send time and identifier
        packet.setStamp(System.currentTimeMillis());
        if (packet.getUniqueId() == null) {
            packet.setUniqueId(UUID.nameUUIDFromBytes(("Time:" + System.nanoTime()).getBytes(Charsets.UTF_8)));
        }
        channel.writeAndFlush(packet);

        // releases the packetbuf
        try {
            packet.getBuf().release();
        } catch(Exception e) {
            //
        }

        // call handler event
        for (INetworkAdapter adapter : this.networkConnection.getNetworkAdapters()) {
            adapter.onPacketSend(p);
        }

        // packet content
        if (logging) {
            String id = (packet.getUniqueId() + "").substring(0, 2);

            System.out.println("[Outgoing " + id + "] '" + packet.getClass().getName() + "'");
        }
    }

}
