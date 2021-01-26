package de.lystx.cloudsystem.library.elements.other;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

@Getter
public class SerializableDocument extends HashMap<String, Object> implements Serializable {

    public SerializableDocument append(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public <T> T get(String key, Class<T> t) {
        return (T) this.get(key);
    }

    public static SerializableDocument fromDocument(Document document) {
        SerializableDocument serializableDocument = new SerializableDocument();
        for (String key : document.keys()) {
            serializableDocument.append(key, document.get(key));
        }
        return serializableDocument;
    }

    public Document toDocument() {
        Document document = new Document();
        this.forEach(document::append);
        return document;
    }


}
