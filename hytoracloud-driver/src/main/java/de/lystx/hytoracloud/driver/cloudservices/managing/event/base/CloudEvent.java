
package de.lystx.hytoracloud.driver.cloudservices.managing.event.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * This class just shows that an Object
 * is an Event.
 *
 * You can cancel the Event if you want to and
 * check if it's cancelled
 */
@Setter @Getter
public class CloudEvent implements Serializable {

    /**
     * If the event is cancelled or not
     */
    private boolean cancelled;

    /**
     * If you want to cancel something
     */
    private String targetComponent;
}
