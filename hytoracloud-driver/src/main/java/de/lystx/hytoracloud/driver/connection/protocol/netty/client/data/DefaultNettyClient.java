package de.lystx.hytoracloud.driver.connection.protocol.netty.client.data;

import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.ClientType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

/**
 * Representation of a client connected to the master server
 */
@Getter @AllArgsConstructor @Setter
public class DefaultNettyClient implements INettyClient {

    /**
     * The name of the connected client
     */
    private final String username;

    /**
     * The host of the client (e.g.: localhost)
     */
    private final String host;

    /**
     * The port of the client (e.g.: 4314) (NETTY)
     */
    private final int port;

    /**
     * The type of the client (e.g.: {@link ClientType#SERVER})
     */
    private ClientType type;

    /**
     * The netty channel of the connection
     */
    private Channel channel;

    /**
     * Returns the host and port of the client as {@link InetSocketAddress} object
     *
     * @return The object mentioned above
     */
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(getHost(), getPort());
    }

    /**
     * Sends an {@link IPacket} to only this client
     *
     * @param connection the connection instance
     * @param packet the packet
     */
    public void sendPacket(INetworkConnection connection, IPacket packet) {
        connection.getNetworkBus().processOut(this.channel, packet);
    }
}
