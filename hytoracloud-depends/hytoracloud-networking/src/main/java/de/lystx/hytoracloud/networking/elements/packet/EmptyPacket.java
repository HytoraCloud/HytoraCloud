package de.lystx.hytoracloud.networking.elements.packet;

import de.lystx.hytoracloud.networking.elements.component.Component;

public class EmptyPacket extends Packet {

    @Override
    public final void write(Component component) {
    }

    @Override
    public final void read(Component component) {
    }
}
