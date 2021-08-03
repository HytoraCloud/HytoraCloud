
package de.lystx.hytoracloud.driver.event.handle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum EventPriority {

    HIGH(-1), //Will be called first
    NORMAL(0), // Will be called standard
    LOW(1); //Will be called last

    public final int value;
}
