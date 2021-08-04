package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;

import java.io.IOException;

/**
 * This packet is for the handshake between two instances (at least a bit of authentication)
 */
public class PacketHandshake extends NettyPacket {

    @Override
    public void read(PacketBuffer buf) throws IOException {
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
    }

}
