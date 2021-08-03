package de.lystx.hytoracloud.driver.connection.messenger;

import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface IChannelMessenger {


    /**
     * Registers listener for a given channel
     *
     * @param channel the channel
     * @param consumer the listener as consumer
     */
    void registerChannel(String channel, Consumer<IChannelMessage> consumer);

    /**
     * Unregisters a channel to listen for
     *
     * @param channel the channel
     */
    void unregisterChannel(String channel);

    /**
     * Sends a {@link IChannelMessage} to the receiver of it
     *
     * @param message the message
     */
    default void sendChannelMessage(IChannelMessage message) {
        this.sendChannelMessage(message, message.getReceiver());
    }

    /**
     * Sends a {@link IChannelMessage} to a given {@link Identifiable} receiver
     *
     * @param message the message
     * @param receiver the receiver
     */
    void sendChannelMessage(IChannelMessage message, Identifiable receiver);

    /**
     * Gets a list of all registered channels
     *
     * @return list of channels
     */
    default List<String> getChannel() {
        return new LinkedList<>(getCache().keySet());
    }

    /**
     * The whole cache for channel and listeners
     *
     * @return cache
     */
    Map<String, Consumer<IChannelMessage>> getCache();
}
