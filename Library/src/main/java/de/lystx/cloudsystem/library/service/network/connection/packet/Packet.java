package de.lystx.cloudsystem.library.service.network.connection.packet;

import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Setter;

import java.io.Serializable;

public class Packet implements Serializable {

    private String document = "{}";
    private PacketResult result;

    public Packet append(String key, Object value) {
        Document document = this.document();
        document.append(key, value);
        this.document = document.toString();
        return this;
    }

    public Packet append(Object value) {
        Document document = this.document();
        document.appendAll(value);
        this.document = document.toString();
        return this;
    }

    public void setResult(PacketResult result) {
        this.result = result;
    }

    public PacketResult getResult() {
        return result;
    }

    public Document document() {
        return new Document(this.document);
    }
}
