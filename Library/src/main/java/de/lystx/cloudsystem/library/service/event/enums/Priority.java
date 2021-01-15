
package de.lystx.cloudsystem.library.service.event.enums;

public enum Priority {

    HIGH(-1),
    NORMAL(0),
    LOW(1);

    public final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
