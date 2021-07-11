package de.lystx.hytoracloud.driver.commons.chat;



import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class CloudComponent implements Serializable {

    /**
     * The message of this component
     */
    private final String message;

    /**
     * The list of other components
     */
    private final List<CloudComponent> cloudComponents;

    /**
     * A map containing actions
     */
    private final Map<CloudComponentAction, Object[]> actions;

    /**
     * Constructs a component with a message
     *
     * @param message the message
     */
    public CloudComponent(String message) {
        this.message = message;
        this.actions = new HashMap<>();
        this.cloudComponents = new LinkedList<>();
    }

    /**
     * Adds an events like click or hover
     *
     * @param action the action (if should open website)
     * @param value the value (url for website)
     * @return this component
     */
    public CloudComponent addEvent(CloudComponentAction action, Object... value) {
        this.actions.put(action, value);
        return this;
    }

    /**
     * Adds another component to chain
     *
     * @param cloudComponent the component to add
     * @return this component
     */
    public CloudComponent append(CloudComponent cloudComponent) {
        this.cloudComponents.add(cloudComponent);
        return this;
    }

    @Override
    public String toString() {
        return this.message;
    }

}
