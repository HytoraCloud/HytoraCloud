package de.lystx.hytoracloud.networking.packet.impl;

import de.lystx.hytoracloud.networking.packet.PacketBuffer;

import java.io.IOException;

public class EmptyPacket extends AbstractPacket {

    @Override
    public final void read(PacketBuffer buf) throws IOException {

    }

    @Override
    public final void write(PacketBuffer buf) throws IOException {

    }
}
