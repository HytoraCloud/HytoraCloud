
package de.lystx.hytoracloud.driver.service.managing.event.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum CloudPriority {

    HIGH(-1), //Will be called first
    NORMAL(0), // Will be called standard
    LOW(1); //Will be called last

    public final int value;
}
