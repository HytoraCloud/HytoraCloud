package net.hytora.networking.elements.packet;

import net.hytora.networking.elements.component.Component;

public class EmptyPacket extends HytoraPacket {

    @Override
    public final void write(Component component) {
    }

    @Override
    public final void read(Component component) {
    }
}
