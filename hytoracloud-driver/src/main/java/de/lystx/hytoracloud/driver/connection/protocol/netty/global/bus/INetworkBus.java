package de.lystx.hytoracloud.driver.connection.protocol.netty.global.bus;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import io.netty.channel.Channel;

public interface INetworkBus {

    /**
     * The connection of this bus instance
     */
    INetworkConnection getNetworkConnection();

    /**
     * Enables logging
     */
    void enableLogging();

    /**
     * Disables logging
     */
    void disableLogging();

    /**
     * Processes an {@link IPacket} (first step after receiving)
     *
     * @param channel The channel who sent the packet
     * @param packet  The packet which was sent
     */
    void processIn(Channel channel, IPacket packet);

    /**
     * Processes the packet (last step before sending)
     *
     * @param channel   The channel to send the packet to
     * @param packet    The packet to be sent
     */
    void processOut(Channel channel, IPacket packet);
}
