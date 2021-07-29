package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.component.RepliableComponent;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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
    private final Identifiable receiver;

    /**
     * The sender
     */
    private Identifiable sender;

    public ChannelMessageObject(String key, String channel, long id, JsonObject<?> document, Identifiable receiver) {
        this.key = key;
        this.channel = channel;
        this.id = id;
        this.document = document == null ? "{}" : document.toString();
        this.receiver = receiver;
    }

    public JsonDocument getDocument() {
        return new JsonDocument(this.document);
    }

    @Override
    public IChannelMessage sendQuery() {
        AtomicReference<IChannelMessage> reference = new AtomicReference<>();
        int timeOut = 3000;

        CloudDriver.getInstance().getMessageManager().sendChannelMessage(this);
        CloudDriver.getInstance().getConnection().registerChannelHandler(this.channel, new Consumer<RepliableComponent>() {
            @Override
            public void accept(RepliableComponent repliableComponent) {

                Component component = repliableComponent.getComponent();
                if (component.getRequestID() == getId() && component.has("iMessage")) {
                    reference.set(component.get("iMessage"));
                }
                CloudDriver.getInstance().getConnection().unregisterChannelHandler(channel, this);
            }
        });

        int count = 0;
        while (reference.get() == null && count++ < timeOut) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (count >= timeOut) {
            reference.set(IChannelMessage.builder().build());
        }
        return reference.get();
    }

    @Override @SneakyThrows
    public void respond(IChannelMessage message) {
        message.setId(this.id);
        message.setSender(this.sender);

        Component component = message.toComponent();

        component.setReceiver(message.getSender().getName());
        component.setChannel(this.channel);
        component.put("iMessage", message);

        CloudDriver.getInstance().getConnection().sendComponent(component);
    }

    @Override
    public void respond(JsonDocument document) {
        this.respond(IChannelMessage.builder().document(document).build());
    }

    @Override
    public void respond(String key, JsonDocument document) {
        this.respond(IChannelMessage.builder().key(key).document(document).build());
    }

    @Override @SneakyThrows
    public Component toComponent() {
        Component component = new Component();
        component.setChannel(this.channel);

        //Setting request-id to this id
        Field requestID = component.getClass().getDeclaredField("requestID");
        requestID.setAccessible(true);
        requestID.set(component, this.id);

        component.setReceiver(receiver == null ? "ALL" : receiver.getName());
        component.put("iMessage", this);
        return component;
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
