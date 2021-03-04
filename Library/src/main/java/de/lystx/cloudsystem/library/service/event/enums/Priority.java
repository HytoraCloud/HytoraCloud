
package de.lystx.cloudsystem.library.service.event.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum Priority {

    HIGH(-1),
    NORMAL(0),
    LOW(1);

    public final int value;
}
