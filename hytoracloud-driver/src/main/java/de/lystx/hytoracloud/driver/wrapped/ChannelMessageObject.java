package de.lystx.hytoracloud.driver.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;


import java.lang.reflect.Field;

@Getter @Setter
public class ChannelMessageObject extends WrappedObject<IChannelMessage, ChannelMessageObject> implements IChannelMessage {

    private static final long serialVersionUID = 2342260166510289808L;

    /**
     * The key of this message
     */
    private final String key;

    /**
     * The channel it should be send to
     */
    private final String channel;

    /**
     * The uuid of this message
     */
    private long id;

    /**
     * The document
     */
    private final String document;

    /**
     * The receiver
     */
    private final IdentifiableObject receiver;

    /**
     * The sender
     */
    private IdentifiableObject sender;

    public ChannelMessageObject(String key, String channel, long id, JsonObject<?> document, Identifiable receiver) {
        this.key = key;
        this.channel = channel;
        this.id = id;
        this.document = document == null ? "{}" : document.toString();
        this.receiver = (IdentifiableObject) receiver;
    }

    public JsonDocument getDocument() {
        return new JsonDocument(this.document);
    }

    public void setSender(Identifiable sender) {
        this.sender = (IdentifiableObject) sender;
    }


    public Identifiable getSender() {
        return sender;
    }

    public Identifiable getReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return document;
    }

    @Override
    public Class<ChannelMessageObject> getWrapperClass() {
        return ChannelMessageObject.class;
    }

    @Override
    Class<IChannelMessage> getInterface() {
        return IChannelMessage.class;
    }
}
