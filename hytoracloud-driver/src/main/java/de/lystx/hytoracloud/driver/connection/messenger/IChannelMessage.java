package de.lystx.hytoracloud.driver.connection.messenger;

import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;

import java.io.Serializable;

public interface IChannelMessage extends Serializable {

    /**
     * Creates a new builder
     *
     * @return builder
     */
    static ChannelMessageBuilder builder() {
        return new ChannelMessageBuilder();
    }

    /**
     * Gets a key of this message
     * @return message key
     */
    String getKey();

    /**
     * The channel of this message
     *
     * @return channel
     */
    String getChannel();

    /**
     * The request id of this message
     *
     * @return id
     */
    long getId();

    /**
     * Sets the id of this message
     *
     * @param id the id
     */
    void setId(long id);

    /**
     * The data of this message
     *
     * @return data
     */
    JsonDocument getDocument();

    /**
     * The receiver of this message
     *
     * @return receiver
     */
    Identifiable getReceiver();

    /**
     * The sender of this message
     *
     * @return sender
     */
    Identifiable getSender();

    /**
     * Sets the sender of this message
     *
     * @param sender the sender
     */
    void setSender(Identifiable sender);

}
