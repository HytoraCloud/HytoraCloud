package net.hytora.networking.elements.component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.function.Consumer;

@Getter @AllArgsConstructor
public class RepliableComponent {

    /**
     * The sender instance
     */
    private final ComponentSender sender;

    /**
     * The component
     */
    private final Component component;

    /**
     * Creates a reply to this query
     *
     * @param content the content you want to reply
     */

    @SneakyThrows
    public void reply(Serializable content) {
        if (this.component.getRequestID() != 0) {
            Component component = new Component();

            Field requestID = component.getClass().getDeclaredField("requestID");
            requestID.setAccessible(true);
            requestID.set(component, this.component.getRequestID());

            component.setReceiver(this.component.getSender());
            component.setChannel(this.component.getChannel());
            component.setMessage(content);

            Field reply = component.getClass().getDeclaredField("reply");
            reply.setAccessible(true);
            reply.set(component, true);

            this.sender.sendComponent(component);
        }
    }

    /**
     * Creates a reply to this query
     *
     * @param component the component
     */
    @SneakyThrows
    public void reply(Component component) {

        if (this.component.getRequestID() != 0) {

            Field reply = component.getClass().getDeclaredField("reply");
            reply.setAccessible(true);
            reply.set(component, true);

            Field requestID = component.getClass().getDeclaredField("requestID");
            requestID.setAccessible(true);
            requestID.set(component, this.component.getRequestID());

            component.setReceiver(this.component.getSender());
            component.setChannel(this.component.getChannel());
            this.sender.sendComponent(component);
        }
    }

    /**
     * Creates a reply to this query
     *
     * @param consumer the consumer to handle the reply
     */
    @SneakyThrows
    public void reply(Consumer<Component> consumer) {
        Component component = new Component();
        consumer.accept(component);

        this.reply(component);
    }

}
