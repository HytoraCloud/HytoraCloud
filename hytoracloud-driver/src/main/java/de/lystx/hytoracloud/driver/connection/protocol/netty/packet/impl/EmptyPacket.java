package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;

import java.io.IOException;

public class EmptyPacket extends NettyPacket {

    @Override
    public final void read(PacketBuffer buf) throws IOException {

    }

    @Override
    public final void write(PacketBuffer buf) throws IOException {

    }
}
