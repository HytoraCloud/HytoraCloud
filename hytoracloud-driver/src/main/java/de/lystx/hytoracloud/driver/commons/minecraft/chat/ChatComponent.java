package de.lystx.hytoracloud.driver.commons.minecraft.chat;



import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class ChatComponent implements Serializable {

    private static final long serialVersionUID = 5453309721161616618L;

    /**
     * The message of this component
     */
    private final String message;

    /**
     * The list of other components
     */
    private final List<ChatComponent> chatComponents;

    /**
     * A map containing actions
     */
    private final Map<CloudComponentAction, Object[]> actions;

    /**
     * Constructs a component with a message
     *
     * @param message the message
     */
    public ChatComponent(String message) {
        this.message = message;
        this.actions = new HashMap<>();
        this.chatComponents = new LinkedList<>();
    }

    /**
     * Adds an events like click or hover
     *
     * @param action the action (if should open website)
     * @param value the value (url for website)
     * @return this component
     */
    public ChatComponent addEvent(CloudComponentAction action, Object... value) {
        this.actions.put(action, value);
        return this;
    }

    /**
     * Adds another component to chain
     *
     * @param chatComponent the component to add
     * @return this component
     */
    public ChatComponent append(ChatComponent chatComponent) {
        this.chatComponents.add(chatComponent);
        return this;
    }

    @Override
    public String toString() {
        return this.message;
    }

}
