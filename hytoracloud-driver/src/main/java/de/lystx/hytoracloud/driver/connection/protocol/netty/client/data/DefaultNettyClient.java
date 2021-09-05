package de.lystx.hytoracloud.driver.connection.protocol.netty.client.data;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel.INetworkChannel;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification.ConnectionType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
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
     * The type of the client
     */
    private ConnectionType type;

    /**
     * The netty channel of the connection
     */
    private INetworkChannel channel;

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
        this.channel.sendPacket(packet);
    }
}
