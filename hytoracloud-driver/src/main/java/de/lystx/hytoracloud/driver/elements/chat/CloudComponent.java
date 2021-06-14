package de.lystx.hytoracloud.driver.elements.chat;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class CloudComponent implements Serializable, ThunderObject {

    /**
     * The message of this component
     */
    private String message;

    /**
     * The list of other components
     */
    private List<CloudComponent> cloudComponents;

    /**
     * A map containing actions
     */
    private Map<CloudComponentAction, Object[]> actions;

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

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(message);
        buf.writeInt(cloudComponents.size());
        for (CloudComponent cloudComponent : cloudComponents) {
            buf.writeThunderObject(cloudComponent);
        }

        buf.writeInt(actions.size());
        for (CloudComponentAction cloudComponentAction : actions.keySet()) {
            buf.writeEnum(cloudComponentAction);
            buf.writeInt(actions.get(cloudComponentAction).length);
            for (Object o : actions.get(cloudComponentAction)) {
                buf.writeObject(o);
            }
        }
    }

    @Override
    public void read(PacketBuffer buf) {

        message = buf.readString();
        int size = buf.readInt();
        cloudComponents = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            cloudComponents.add(buf.readThunderObject(CloudComponent.class));
        }

        size = buf.readInt();
        actions = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            CloudComponentAction cloudComponentAction = buf.readEnum(CloudComponentAction.class);
            int length = buf.readInt();
            Object[] objects = new Object[length];
            for (int i1 = 0; i1 < length; i1++) {
                objects[i1] = buf.readObject();
            }
            actions.put(cloudComponentAction, objects);
        }
    }
}
