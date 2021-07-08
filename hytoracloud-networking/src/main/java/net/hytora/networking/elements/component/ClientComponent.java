package net.hytora.networking.elements.component;

import net.hytora.networking.connection.client.HytoraClient;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;

@Getter @AllArgsConstructor
public class ClientComponent implements ReplyComponent {

    /**
     * The client
     */
    private final HytoraClient connection;

    /**
     * The message
     */
    private final Component component;


    /**
     * Reply to this component
     * with a given content
     *
     * @param content the content
     */
    @Override
    public void reply(Object content) {
        if (this.component.getIdRequest() != 0) {
            Component component = new Component();

            component.setIdRequest(this.component.getIdRequest());
            component.setRecipient(this.component.getSender());
            component.setChannel(this.component.getChannel());
            component.setReply(true);
            component.setContent(content);
            this.connection.reply(component);
        }
    }

    /**
     * Reply to this component
     * with a given consumer
     *
     * @param consumer the consumer
     */
    @Override
    public void reply(Consumer<Component> consumer) {
        Component component = new Component();
        consumer.accept(component);

        if (this.component.getIdRequest() != 0) {
            component.setIdRequest(this.component.getIdRequest());
            component.setRecipient(this.component.getSender());
            component.setReply(true);
            component.setChannel(this.component.getChannel());
            this.connection.reply(component);
        }
    }

}

