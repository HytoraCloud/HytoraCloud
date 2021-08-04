package de.lystx.hytoracloud.driver.connection.protocol.netty.client.data;

import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.ClientType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;


public interface INettyClient {

    /**
     * The name of the client
     */
    String getUsername();

    /**
     * The host of the client (e.g.: localhost)
     */
    String getHost();

    /**
     * The port of the client (e.g.: 4314) (NETTY)
     */
    int getPort();

    /**
     * The type of the client (e.g.: {@link ClientType#SERVER})
     */
    ClientType getType();

    /**
     * The netty {@link Channel}
     */
    Channel getChannel();

    /**
     * Sets the channel of this client
     *
     * @param channel the channel
     */
    void setChannel(Channel channel);

    /**
     * Returns the host and port of the client as {@link InetSocketAddress} object
     *
     * @return The object mentioned above
     */
    InetSocketAddress getAddress();

    void sendPacket(INetworkConnection connection, IPacket packet);
}
