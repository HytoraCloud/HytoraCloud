package de.lystx.cloudsystem.library.service.network.connection.packet;

import de.lystx.cloudsystem.library.elements.other.Document;

import java.io.Serializable;

public class Packet implements Serializable {

    //Document serialisation via String
    protected String document = "{}";

    /**
     * Appends a value to the packet document
     * @param key
     * @param value
     * @return current packet
     */
    public Packet append(String key, Object value) {
        this.document = this.document().append(key, value).toString();
        return this;
    }

    /**
     * Appeding whole value to packet
     * @param value
     * @return current packet
     */
    public Packet append(Object value) {
        this.document = this.document().append(value).toString();
        return this;
    }

    /**
     * Gets packet as provided class object
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getAs(Class<T> tClass) {
        return this.document().getAs(tClass);
    }

    /**
     * Transforms packet to document
     * @return Document
     */
    public Document document() {
        return new Document(this.document);
    }
}
