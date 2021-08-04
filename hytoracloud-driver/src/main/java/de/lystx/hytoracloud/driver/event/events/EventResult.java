package de.lystx.hytoracloud.driver.event.events;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EventResult {

    /**
     * If the event should be cancelled
     */
    private boolean cancelled;

    /**
     * The component (e.g. for cancel reasons)
     */
    private String component;

    /**
     * Other objects
     */
    private Object[] objects;

    public void setObjects(Object... objects) {
        this.objects = objects;
    }

    public EventResult() {
        this.cancelled = false;
        this.component = "";
        this.objects = new Object[999];
    }

    public EventResult(boolean cancelled, String component) {
        this.cancelled = cancelled;
        this.component = component;
        this.objects = new Object[999];
    }

    public EventResult(boolean cancelled, String component, Object... objects) {
        this.cancelled = cancelled;
        this.component = component;
        this.objects = objects;
    }
}
