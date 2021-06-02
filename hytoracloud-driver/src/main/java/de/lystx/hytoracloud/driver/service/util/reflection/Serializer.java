package de.lystx.hytoracloud.driver.service.util.reflection;

import java.io.*;

/**
 * This Class is used to
 * serialize and deserialize Objects
 *
 * You enter a value and you will get back
 * a byte array of it
 * You can easiliy send this byte array between sockets (Netty)
 * and recreate the object with the {@link Serializer#deserialize(byte[])} Method
 *
 * @param <T> the GenericType you want to serialize
 */
public class Serializer<T> implements Serializable {

    private final T t;

    public Serializer(T t) {
        this.t = t;
    }

    public Serializer() {
        this(null);
    }

    /**
     * Recreates file with byte[] s
     * @param data
     * @return T to use
     */
    public T deserialize(byte[] data) {
        try {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(data); ObjectInputStream in = new ObjectInputStream(bis)) {
                return (T) in.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Serialiases current File to byte[]
     * @return bytes of T to recreate
     */
    public byte[] serialize() {
        try {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream out = new ObjectOutputStream(bos)) {
                 out.writeObject(this.t);
                 return bos.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
