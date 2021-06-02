package de.lystx.hytoracloud.networking.packet;

import com.google.common.base.Charsets;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PacketBuffer {

    /**
     * The {@link ByteBuf} to read all data from
     */
    private final ByteBuf buf;

    /**
     * The maximum length of a string
     */
    private static final int MAX_STRING_LENGTH = 32767;

    /**
     * Checks for nullSafety
     */
    private boolean nullSafe;

    /**
     * Skips the buf and all its bytes
     */
    public void skip() {
        this.buf.skipBytes(buf.readableBytes());
    }

    /**
     * Reads var int from buffer
     *
     * @return the value
     */
    public int readVarInt() {
        if (this.checkNullSafe()) {
            return -1;
        }
        int i = 0;
        int j = 0;

        while(true){
            byte b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;

            if(j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    /**
     * Writes a var int to buffer
     *
     * @param i the int
     */
    public void writeVarInt(int i) {
        if (this.checkNullSafe(i)) {
            while ((i & -128) != 0) {
                buf.writeByte(i & 127 | 128);
                i >>>= 7;
            }

            buf.writeByte(i);
        }
    }

    /**
     * Writes a uniqueId to the buffer
     *
     * @param uniqueId the uuid
     */
    public void writeUUID(UUID uniqueId) {
        if (this.checkNullSafe(uniqueId)) {
            this.writeLong(uniqueId.getMostSignificantBits());
            this.writeLong(uniqueId.getLeastSignificantBits());
        }
    }

    /**
     * Reads a {@link UUID} from the buffer
     *
     * @return read uuid
     */
    public UUID readUUID() {
        if (checkNullSafe()) {
            return null;
        }
        return new UUID(readLong(), readLong());
    }

    /**
     * Writes an enum value to the buffer
     *
     * @param val the enum
     */
    public void writeEnum(Enum<?> val) {
        this.writeVarInt(val.ordinal());
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

    /**
     * Reads a string with default maxLength of 32767
     *
     * @return string
     */
    public String readString() {
        if (checkNullSafe()) {
            return null;
        }
        return readString(MAX_STRING_LENGTH);
    }

    /**
     * Writes a string into the packets buffer
     *
     * @param s The string
     */
    public void writeString(String s) {
        if (this.checkNullSafe(s)) {

            byte[] bytes = s.getBytes(Charsets.UTF_8);
            if (bytes.length > MAX_STRING_LENGTH) {
                throw new EncoderException("String too big (was " + s.length() + " bytes encoded, slots " + 32767 + ")");
            } else {
                this.writeVarInt(bytes.length);
                this.writeBytes(bytes);
            }
        }

    }

    /**
     * Writes bytes to the buffer
     *
     * @param bytes the byte-array
     */
    public void writeBytes(byte[] bytes) {
        this.buf.writeBytes(bytes);
    }

    /**
     * Reads a stringList from the byteBuf
     *
     * @return The list
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
     * Writes a stringList into the buffer
     *
     * @param list The list
     */
    public void writeStringList(List<String> list) {
        this.writeVarInt(list.size());
        list.forEach(this::writeString);
    }

    /**
     * Reads a Long from the buffer
     *
     * @return long
     */
    public long readLong() {
        if (checkNullSafe()) {
            return -1L;
        }
        return buf.readLong();
    }

    /**
     * Writes a long to the buffer
     *
     * @param l the long
     */
    public void writeLong(long l) {
        if (this.checkNullSafe(l)) {
            buf.writeLong(l);
        }
    }

    /**
     * Writes an int from the buffer
     *
     * @return the value
     */
    public int readInt() {
        if (checkNullSafe()) {
            return -1;
        }
        return buf.readInt();
    }

    /**
     * Writes an int to the buffer
     *
     * @param i the int
     */
    public void writeInt(int i) {
        if (this.checkNullSafe(i)) {
            buf.writeInt(i);
        }
    }

    /**
     * Reads a boolean from the buffer
     *
     * @return the boolean
     */
    public boolean readBoolean() {
        if (checkNullSafe()) {
            return false;
        }
        return buf.readBoolean();
    }

    /**
     * Writes a boolean to the buffer
     *
     * @param b the boolean
     */
    public void writeBoolean(boolean b) {
        if (this.checkNullSafe(b)) {
            buf.writeBoolean(b);
        }
    }


    /**
     * Reads the current byte
     * @return current byte
     */
    @SneakyThrows
    public synchronized byte readByte() {
        if (checkNullSafe()) {
            return -1;
        }
        return buf.readByte();
    }

    /**
     * Reads the current double
     * @return current double
     */
    @SneakyThrows
    public synchronized double readDouble() {
        if (checkNullSafe()) {
            return -1;
        }
        return buf.readDouble();
    }

    /**
     * Reads the current float
     * @return current float
     */
    @SneakyThrows
    public synchronized float readFloat() {
        if (checkNullSafe()) {
            return -1;
        }
        return buf.readFloat();
    }

    /**
     * Reads the current short
     * @return current short
     */
    @SneakyThrows
    public synchronized short readShort() {
        if (checkNullSafe()) {
            return -1;
        }
        return buf.readShort();
    }

    /**
     * Reads the current char
     * @return current char
     */
    @SneakyThrows
    public synchronized char readChar() {
        if (checkNullSafe()) {
            return 'N';
        }
        return buf.readChar();
    }


    /**
     * This will read custom values
     * from InputStream (maybe serialized)
     *
     * @return Object from Stream
     */
    @SneakyThrows
    public synchronized <T> T readObject() {
        if (checkNullSafe()) {
            return null;
        }
        String objectClass = this.readString();
        return new JsonBuilder(this.readString()).getAs((Class<T>) Class.forName(objectClass));
    }


    /**
     * Checks if nullSafe is active
     * if its active and value is null
     * you can return null
     * else you can return the not-null-value
     *
     * @return boolean
     */
    private boolean checkNullSafe() {
        if (this.nullSafe) {
            this.nullSafe = false;
            try {
                String s = readString();
                return s.equals("_null_");
            } catch (Exception e) {
                //Ignoring
            }
        }
        return false;
    }

    /**
     * Activates nullSafe
     *
     * @return current buffer
     */
    public PacketBuffer nullSafe() {
        this.nullSafe = true;
        return this;
    }

    private boolean checkNullSafe(Object object) {
        if (this.nullSafe) {
            this.nullSafe = false;
            if (object == null) {
                this.writeString("_null_");
                return false;
            } else {
                this.writeString("_allRight_");
                return true;
            }
        }
        return false;
    }
}
