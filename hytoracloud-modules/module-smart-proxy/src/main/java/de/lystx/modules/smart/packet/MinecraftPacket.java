package de.lystx.modules.smart.packet;

import io.netty.buffer.ByteBuf;
import lombok.Setter;

import java.nio.channels.Channel;

@Setter
public abstract class MinecraftPacket {

    /**
     * The id of this packet
     *
     * @return id as int
     */
    public abstract int getId();


    /**
     * Writes the minecraft packet
     *
     * @param buf the buf to write data to
     */
    public abstract void write(PacketBuffer buf);

    /**
     * Reads the minecraft packet
     *
     * @param buf the buf to read data from
     */
    public abstract void read(PacketBuffer buf);

    /**
     * Called when its built
     * and can be handled
     */
    public abstract void handle();


    public final void send(Channel channel) {

    }


    public int readVarInt(ByteBuf buf) {
        return readVarInt(5, buf);
    }

    public int readVarInt(int maxBytes, ByteBuf buf) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = buf.readByte();
            out |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > maxBytes) throw new RuntimeException("VarInt too big");
            if ((in & 0x80) != 0x80) break;
        }
        return out;
    }

    public String readString(ByteBuf buf) {
        int len = readVarInt(buf);
        byte[] b = new byte[len];
        buf.readBytes(b);
        return new String(b);
    }

    public void writeVarInt(int value, ByteBuf buf) {
        int part;
        while (true) {
            part = value & 0x7F;
            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }
            buf.writeByte(part);
            if (value == 0) break;
        }
    }

    public void writeString(String s, ByteBuf buf) {
        byte[] b = s.getBytes();
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

}
