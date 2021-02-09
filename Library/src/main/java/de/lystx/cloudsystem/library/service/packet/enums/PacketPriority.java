
package de.lystx.cloudsystem.library.service.packet.enums;

public enum PacketPriority {

    HIGH(-1),
    NORMAL(0),
    LOW(1);

    public final int value;

    PacketPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
