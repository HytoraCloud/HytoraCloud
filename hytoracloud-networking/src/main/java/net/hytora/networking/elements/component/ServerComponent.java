package net.hytora.networking.elements.component;


import net.hytora.networking.connection.HytoraConnectionBridge;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;

@AllArgsConstructor @Getter
public class ServerComponent implements ReplyComponent {

    /**
     * The client instance
     */
    private final HytoraConnectionBridge connection;

    /**
     * The message
     */
    private final Component component;

    /**
     * Creates a reply to this query
     *
     * @param content the content you want to reply
     */
    @Override
    public void reply(Object content) {
        if (this.component.getIdRequest() != 0) {
            Component component = new Component();
            component.setIdRequest(this.component.getIdRequest());
            component.setRecipient(this.component.getSender());
            component.setChannel(this.component.getChannel());
            component.setContent(content);
            component.setReply(true);
            this.connection.sendReply(component);
        }
    }

    /**
     * Creates a reply to this query
     *
     * @param consumer the consumer to handle the reply
     */
    @Override
    public void reply(Consumer<Component> consumer) {
        Component component = new Component();
        consumer.accept(component);

        if (this.component.getIdRequest() != 0) {
            component.setReply(true);
            component.setIdRequest(this.component.getIdRequest());
            component.setRecipient(this.component.getSender());
            component.setChannel(this.component.getChannel());
            this.connection.sendReply(component);
        }
    }


}
