package de.lystx.hytoracloud.driver.cloudservices.global.messenger;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.wrapped.Identification;
import de.lystx.hytoracloud.driver.commons.wrapped.ChannelMessageObject;

import java.util.Random;

public class ChannelMessageBuilder {

    /**
     * The channel
     */
    private String channel;

    /**
     * The key
     */
    private String key;

    /**
     * The data
     */
    private JsonObject<?> document;

    /**
     * The receiver
     */
    private Identifiable receiver = Identifiable.ALL;

    /**
     * Sets the channel of this message
     *
     * @param channel the channel
     * @return current builder
     */
    public ChannelMessageBuilder channel(String channel) {
        this.channel = channel;
        return this;
    }

    /**
     * Sets the key of this message
     *
     * @param key the key
     * @return current builder
     */
    public ChannelMessageBuilder key(String key) {
        this.key = key;
        return this;
    }

    /**
     * Sets the document of this message
     *
     * @param document the document
     * @return current builder
     */
    public ChannelMessageBuilder document(JsonObject<?> document) {
        this.document = document;
        return this;
    }

    /**
     * Sets the receiver of this message
     *
     * @param receiver the receiver
     * @return current builder
     */
    public ChannelMessageBuilder receiver(Identifiable receiver) {
        this.receiver = receiver;
        return this;
    }

    /**
     * Builds the {@link IChannelMessage}
     *
     * @return built message
     */
    public IChannelMessage build() {
        IChannelMessage message = new ChannelMessageObject(key, this.channel, new Random().nextLong(), this.document, this.receiver);
        message.setSender(new Identification(CloudDriver.getInstance().getConnection().getName()));
        return message;
    }
}
