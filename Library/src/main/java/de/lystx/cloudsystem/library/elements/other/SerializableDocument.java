package de.lystx.cloudsystem.library.elements.other;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

@Getter
public class SerializableDocument extends HashMap<String, Object> implements Serializable {

    public SerializableDocument append(String key, Object value) {
        if (value instanceof Document) {
            this.put(key, ((Document) value).getJsonObject());
        } else {
            this.put(key, value);
        }
        return this;
    }


    public SerializableDocument append(Object value) {
        this.clear();
        JsonObject jsonObject = new Gson().toJsonTree(value).getAsJsonObject();
        jsonObject.keySet().forEach(key -> {
            this.put(key, jsonObject.get(key));
        });
        return this;
    }

    public <T> T get(String key, Class<T> t) {
        return (T) this.get(key);
    }


    public boolean has(String key) {
        return this.containsKey(key);
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

    public String toString() {
        return this.toDocument().toString();
    }
}
