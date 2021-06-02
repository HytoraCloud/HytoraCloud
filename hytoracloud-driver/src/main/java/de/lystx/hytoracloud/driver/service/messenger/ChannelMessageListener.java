package de.lystx.hytoracloud.driver.service.messenger;

import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;

public interface ChannelMessageListener {

    /**
     * Called when a {@link ChannelMessage} was received
     *
     * @param identifier the identifier to identify the message
     * @param message the message
     * @param data the data
     * @param targetComponents extra components
     */
    void onReceiveMessage(String identifier, JsonBuilder data, String[] targetComponents);

    /**
     * Only used to make this easier to handle
     *
     * @param channelMessage the message
     */
    default void onReceiveRaw(ChannelMessage channelMessage) {
        this.onReceiveMessage(channelMessage.getIdentifier(), channelMessage.getJsonBuilder(), channelMessage.getTargetComponents());
    }
}
