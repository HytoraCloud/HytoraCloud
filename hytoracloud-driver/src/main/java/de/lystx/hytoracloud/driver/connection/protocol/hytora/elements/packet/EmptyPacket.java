package de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;

public class EmptyPacket extends Packet {

    @Override
    public final void write(Component component) {
    }

    @Override
    public final void read(Component component) {
    }
}
