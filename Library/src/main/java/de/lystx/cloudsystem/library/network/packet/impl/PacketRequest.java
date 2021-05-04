package de.lystx.cloudsystem.library.network.packet.impl;

import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * This packet is to request specific values from smth or someone
 * OR just to trigger
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketRequest extends AbstractPacket {


    public Type type;
    public String meta;

    public PacketRequest(Type type) {
        this(type, "");
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        this.type = buf.readEnumValue(Type.class);
        this.meta = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(type);
        buf.writeString(meta);
    }

    public enum Type {

        PING

    }

}
