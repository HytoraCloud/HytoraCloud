package de.lystx.cloudsystem.library.service.network.connection.packet;

import de.lystx.cloudsystem.library.elements.other.Document;

import java.io.Serializable;

public class Packet implements Serializable {

    protected String document = "{}";

    public Packet append(String key, Object value) {
        this.document = this.document().append(key, value).toString();
        return this;
    }

    public Packet append(Object value) {
        this.document = this.document().append(value).toString();
        return this;
    }

    public <T> T getAs(Class<T> tClass) {
        return this.document().getAs(tClass);
    }

    public Document document() {
        return new Document(this.document);
    }
}
