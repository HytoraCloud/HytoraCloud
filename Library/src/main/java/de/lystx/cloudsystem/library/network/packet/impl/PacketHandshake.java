package de.lystx.cloudsystem.library.network.packet.impl;

import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import de.lystx.cloudsystem.library.network.packet.PacketBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * This packet is for the handshake between two instances (at least a bit of authentication)
 */
@NoArgsConstructor
@AllArgsConstructor

public class PacketHandshake extends AbstractPacket {

    public String identifier;
    public InetSocketAddress inetSocketAddress;

    @Override
    public void read(PacketBuffer buf) throws IOException {
        this.identifier = buf.readString();
        this.inetSocketAddress = new InetSocketAddress(buf.readString(), buf.readInt());
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeString(this.identifier);
        buf.writeString(this.inetSocketAddress.getHostString());
        buf.writeInt(this.inetSocketAddress.getPort());
    }

}
