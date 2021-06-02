
package de.lystx.hytoracloud.driver.service.event;

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
public class CloudEvent {

    private boolean cancelled;
}
