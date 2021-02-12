package de.lystx.cloudsystem.library.service.network.connection.packet;

import de.lystx.cloudsystem.library.elements.other.Document;

import java.io.Serializable;

public class Packet implements Serializable {

    public static final long serialVersionUID = -3040096452457271695L;
    private String document = "{}";

    public Packet append(String key, Object value) {
        Document document = this.document();
        document.append(key, value);
        this.document = document.toString();
        return this;
    }

    public Packet append(Object value) {
        Document document = this.document();
        document.append(value);
        this.document = document.toString();
        return this;
    }


    public Document document() {
        return new Document(this.document);
    }
}
