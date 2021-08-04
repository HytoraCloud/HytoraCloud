package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Wrapper class for a {@link ByteBuf}
 */
@AllArgsConstructor
public class PacketBuffer {

    private final ByteBuf buf;

    /**
     * Read var int from buffer
     *
     * @return The integer
     */
    public int readVarInt() {
        int i = 0;
        int j = 0;

        while(true){
            byte b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;

            if(j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    /**
     * Write var int to buffer
     *
     * @param input The input
     */
    public PacketBuffer writeVarInt(int input) {
        while((input & -128) != 0){
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        buf.writeByte(input);
        return this;
    }

    /**
     * Writes a uniqueId to the buffer
     *
     * @param uuid The uuid
     * @return This
     */
    public PacketBuffer writeUUID(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    /**
     * Writes an enum value to the buffer
     *
     * @param val The value
     * @return This
     */
    public PacketBuffer writeEnum(Enum<?> val) {
        this.writeVarInt(val.ordinal());
        return this;
    }

    /**
     * Reads an enum value from the buffer
     *
     * @param enumClass The enum's class
     * @param <T>       The enum type
     * @return The enum object
     */
    public <T extends Enum<T>> T readEnum(Class<T> enumClass) {
        return enumClass.getEnumConstants()[this.readVarInt()];
    }

    /**
     * Read string from buffer
     *
     * @param maxLength The slots length
     * @return The successful
     */
    public String readString(int maxLength) {
        int i = this.readVarInt();

        if(i > maxLength * 4 || i < 0) {
            throw new DecoderException("The received encoded string buffer length is not allowed!");
        }
        else {
            ByteBuf part = buf.readBytes(i);
            String s = part.toString(Charsets.UTF_8);

            if(s.length() > maxLength) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
            }
            else {
                return s;
            }
        }
    }

    public String readString() {
        return readString(32767);
    }

    /**
     * Writes a string into the packets buffer
     *
     * @param string The string
     * @return The buffer
     */
    public PacketBuffer writeString(String string) {
        if(string == null) string = "";

        byte[] abyte = string.getBytes(Charsets.UTF_8);

        if(abyte.length > 32767) {
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, slots " + 32767 + ")");
        }
        else {
            this.writeVarInt(abyte.length);
            buf.writeBytes(abyte);
            return this;
        }
    }

    /**
     * Reads a stringList from the byteBuf
     *
     * @return The stringlist
     */
    public List<String> readStringList() {
        int size = readVarInt();
        List<String> l = new ArrayList<>(size);

        for(int i = 0; i < size; i++) {
            l.add(readString());
        }
        return l;
    }

    /**
     * Writes a stringList into the database
     *
     * @param l The list
     * @return This
     */
    public PacketBuffer writeStringList(List<String> l) {
        this.writeVarInt(l.size());
        l.forEach(this::writeString);
        return this;
    }

    public long readLong() {
        return buf.readLong();
    }

    public void writeLong(long l) {
        buf.writeLong(l);
    }

    public int readInt() {
        return buf.readInt();
    }

    public void writeInt(int i) {
        buf.writeInt(i);
    }

    public boolean readBoolean() {
        return buf.readBoolean();
    }

    public void writeBoolean(boolean b) {
        buf.writeBoolean(b);
    }

}
