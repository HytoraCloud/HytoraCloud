package de.lystx.cloudsystem.library.elements.other;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;

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

    public SerializableDocument getDocument(String key) {
        SerializableDocument serializableDocument = new SerializableDocument();
        serializableDocument.append(this.get(key));
        return serializableDocument;
    }

    public SerializableDocument append(Object value) {
        this.clear();
        JsonObject jsonObject = new Gson().toJsonTree(value).getAsJsonObject();
        jsonObject.keySet().forEach(key -> {
            this.put(key, jsonObject.get(key));
        });
        return this;
    }
    public SerializableDocument increase(String key) {
        this.append(key, (this.getInteger(key) + 1));
        return this;
    }

    public <T> T get(String key, Class<T> t) {
        return this.toDocument().getObject(key, t);
    }

    public Integer getInteger(String key) {
        return ((JsonPrimitive) this.get(key)).getAsInt();
    }

    public Boolean getBoolean(String key) {
        return ((JsonPrimitive) this.get(key)).getAsBoolean();
    }

    public String getString(String key) {
        return ((JsonPrimitive) this.get(key)).getAsString();
    }

    public java.lang.Double getDouble(String key) {
        return ((JsonPrimitive) this.get(key)).getAsDouble();
    }

    public Short getShort(String key) {
        return ((JsonPrimitive) this.get(key)).getAsShort();
    }

    public Long getLong(String key) {
        return ((JsonPrimitive) this.get(key)).getAsLong();
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
