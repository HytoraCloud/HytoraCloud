package de.lystx.hytoracloud.driver.elements.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class EventResult {

    private boolean cancelled;
    private String component;
    private Object[] objects;

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
