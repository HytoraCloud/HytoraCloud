package de.lystx.cloudsystem.library.service.util;

import java.io.*;

public class Serializer<T> implements Serializable{

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
