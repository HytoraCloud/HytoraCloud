package de.lystx.cloudsystem.library.elements.chat;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class CloudComponent implements Serializable {

    private final String message;
    private final List<CloudComponent> cloudComponents;
    private final Map<CloudComponentAction, Object[]> actions;

    public CloudComponent(String message) {
        this.message = message;
        this.actions = new HashMap<>();
        this.cloudComponents = new LinkedList<>();
    }

    /**
     * Adds events like click or hover
     * @param action
     * @param value
     * @return
     */
    public CloudComponent addEvent(CloudComponentAction action, Object... value) {
        this.actions.put(action, value);
        return this;
    }

    /**
     * Adds another component to chain
     * @param cloudComponent
     * @return
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
