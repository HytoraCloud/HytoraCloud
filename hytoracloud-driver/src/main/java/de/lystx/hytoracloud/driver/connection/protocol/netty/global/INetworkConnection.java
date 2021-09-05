package de.lystx.hytoracloud.driver.connection.protocol.netty.global;

import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.bus.INetworkBus;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel.INetworkChannel;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification.IPacketSender;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.handling.IChannelHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification.ConnectionType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.handling.INetworkAdapter;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.bus.NetworkBusObject;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.IRequestManager;
import io.netty.channel.Channel;

import java.io.IOException;
import java.util.List;

public interface INetworkConnection extends IPacketSender {

    /**
     * Gets the current netty {@link Channel}
     *
     * @return channel
     */
    INetworkChannel getChannel();

    /**
     * The type of this connection
     * @return the type
     */
    ConnectionType getType();

    /**
     * Gets the current {@link IRequestManager} for this instance
     * to manage all in- and outgoing requests and queries
     *
     * @return the manager instance
     */
    IRequestManager getRequestManager();

    /**
     * Sends a {@link IPacket} to a specific {@link Channel}
     *
     * @param packet the packet
     * @param channel the channel
     */
    void sendPacket(IPacket packet, Channel channel);

    /**
     * Starts the connection
     *
     */
    void bootstrap();

    /**
     * Shuts down the connection
     */
    void shutdown() throws IOException;

    /**
     * Registers a {@link IPacketHandler}
     *
     * @param packetHandler the handler
     */
    void registerPacketHandler(IPacketHandler packetHandler);

    /**
     * Unregisters an {@link IPacketHandler}
     *
     * @param packetHandler the handler
     */
    void unregisterPacketHandler(IPacketHandler packetHandler);

    /**
     * Gets al ist of all registered {@link IPacketHandler}s
     *
     * @return list of handlers
     */
    List<IPacketHandler> getPacketHandlers();

    /**
     * Registers a {@link INetworkAdapter}
     *
     * @param adapter the adapter
     */
    void registerNetworkAdapter(INetworkAdapter adapter);

    /**
     * Gets a list of all {@link INetworkAdapter}
     *
     * @return list
     */
    List<INetworkAdapter> getNetworkAdapters();

    /**
     * Registers an {@link IChannelHandler}
     *
     * @param channel the channel name
     * @param channelHandler the handler
     */
    void registerChannelHandler(String channel, IChannelHandler channelHandler);

    /**
     * All registered {@link IChannelHandler}s
     *
     * @return list
     */
    List<IChannelHandler> getChannelHandlers();

    /**
     * All registered {@link IChannelHandler}s for a given channel
     *
     * @param channel the channel
     * @return list
     */
    List<IChannelHandler> getChannelHandlers(String channel);

    /**
     * Sends an {@link IChannelMessage} message
     *
     * @param message the message
     */
    void sendChannelMessage(IChannelMessage message);

    /**
     * The {@link NetworkBusObject} for processing
     * {@link IPacket}s in and out
     *
     * @return bus
     */
    INetworkBus getNetworkBus();

    /**
     * Checks if connected
     *
     * @return boolean
     */
    boolean isConnected();

}
