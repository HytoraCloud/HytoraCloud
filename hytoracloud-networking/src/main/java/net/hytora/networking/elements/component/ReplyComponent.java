package net.hytora.networking.elements.component;

import net.hytora.networking.connection.HytoraConnection;

import java.util.function.Consumer;

public interface ReplyComponent {

    /**
     * Reply to this component
     * with a given content
     *
     * @param content the content
     */
    void reply(Object content);

    /**
     * Reply to this component
     * with a given consumer
     *
     * @param consumer the consumer
     */
    void reply(Consumer<Component> consumer);

    /**
     * Gets the {@link Component} of this reply
     *
     * @return component
     */
    Component getComponent();
}
