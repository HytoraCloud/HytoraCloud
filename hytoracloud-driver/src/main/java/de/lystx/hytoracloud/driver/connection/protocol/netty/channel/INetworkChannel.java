package de.lystx.hytoracloud.driver.connection.protocol.netty.channel;

import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.module.base.info.ScheduledModuleTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.SocketAddress;

public interface INetworkChannel {

    /**
     * The netty channel instance
     *
     * @return instance
     */
    Channel nettyVariant();

    /**
     * The connection of this channel
     *
     * @return connection instance
     */
    INetworkConnection getNetworkConnection();

    /**
     * Sends an {@link IPacket} to this channel
     *
     * @param packet the packet
     */
    void sendPacket(IPacket packet);

    /**
     * Checks if channel is open
     */
    boolean isOpen();

    /**
     * Checks if channel is registered
     */
    boolean isRegistered();

    /**
     * Checks if channel is active
     */
    boolean isActive();

    /**
     * Checks if channel is writable
     */
    boolean isWritable();

    /**
     * The local address of the channel
     */
    SocketAddress localAddress();

    /**
     * The local remoteAddress of the channel
     */
    SocketAddress remoteAddress();

    /**
     * Reads the channel
     */
    INetworkChannel read();

    /**
     * Flushes the channel
     */
    INetworkChannel flush();

    /**
     * Writes and flushes an object
     *
     * @param obj the object
     * @return current channel
     */
    INetworkChannel writeAndFlush(Object obj);

    /**
     * Writes an object
     *
     * @param obj the object
     * @return current channel
     */
    INetworkChannel write(Object obj);

    /**
     * Disconnects
     */
    void disconnect();

    /**
     * Deregisters
     */
    void deregister();

    /**
     * Closes this channel
     */
    void close();

    /**
     * Closes this channel
     *
     * @return the future
     */
    ChannelFuture closeFuture();
}
