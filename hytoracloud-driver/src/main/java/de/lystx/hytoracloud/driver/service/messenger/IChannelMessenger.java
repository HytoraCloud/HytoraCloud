package de.lystx.hytoracloud.driver.service.messenger;


import de.lystx.hytoracloud.driver.elements.other.JsonEntity;

import java.util.List;

public interface IChannelMessenger {

    /**
     * Returns all {@link ChannelMessageListener}s
     *
     * @return list of listeners
     */
    List<ChannelMessageListener> getAllListeners();

    /**
     * Returns all {@link ChannelMessageListener}s of a channel
     *
     * @param channel the name of the channel
     * @return list of listeners
     */
    List<ChannelMessageListener> getListenersOfChannel(String channel);

    /**
     * Registers a {@link ChannelMessageListener} for a given Channel
     *
     * @param channel the name of the channel
     * @param listener the listener
     */
    void registerChannelListener(String channel, ChannelMessageListener listener);

    /**
     * Unregisters a {@link ChannelMessageListener} from all channels
     *
     * @param listener the listener
     */
    void unregisterChannelListener(ChannelMessageListener listener);

    /**
     * Sends a {@link ChannelMessage} (Raw-Method)
     *
     * @param channelMessage the message
     */
    void sendChannelMessage(ChannelMessage channelMessage);

    /**
     * Handles the incoming Message
     *
     * @param channelMessage the message to handle
     */
    default void handleIn(ChannelMessage channelMessage) {

    }

    /**
     * Sends a {@link ChannelMessage} and constructs it
     *
     * @param identifier the identifier to identify it
     * @param data the data
     * @param targetComponents the target components
     */
    default void sendChannelMessage(String channel, String identifier, JsonEntity data, String... targetComponents) {
        this.sendChannelMessage(new ChannelMessage(channel, data, identifier, targetComponents));
    }

    /**
     * Sends a {@link ChannelMessage} to all proxys
     *
     * @param identifier the identifier to identify it
     * @param data the data
     */
    default void sendProxyChannelMessage(String channel, String identifier, JsonEntity data) {
        this.sendChannelMessage(new ChannelMessage(channel, data, identifier, "only_proxy"));
    }
    /**
     * Sends a {@link ChannelMessage} to all bukkits
     *
     * @param identifier the identifier to identify it
     * @param data the data
     */
    default void sendBukkitChannelMessage(String channel, String identifier, JsonEntity data) {
        this.sendChannelMessage(new ChannelMessage(channel, data, identifier, "only_bukkit"));
    }

}
