
package de.lystx.cloudsystem.library.service.network.packet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum PacketPriority {

    HIGH(-1),
    NORMAL(0),
    LOW(1);

    public final int value;

}
