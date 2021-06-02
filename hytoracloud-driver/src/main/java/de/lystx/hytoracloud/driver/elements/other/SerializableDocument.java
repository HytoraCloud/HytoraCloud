package de.lystx.hytoracloud.driver.elements.other;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter

/*
 * Can be send in packets
 * Map is serializable
 * Document uses Gson which isn't
 */
public class SerializableDocument extends HashMap<String, Object> implements Serializable {

    /**
     * Adds value
     * @param key
     * @param value
     * @return
     */
    public SerializableDocument append(String key, Object value) {
        if (value instanceof JsonBuilder) {
            this.put(key, ((JsonBuilder) value).getJsonObject());
        } else {
            this.put(key, value);
        }
        return this;
    }

    /**
     * Returns Document from key
     * @param key
     * @return
     */
    public SerializableDocument getDocument(String key) {
        SerializableDocument serializableDocument = new SerializableDocument();
        serializableDocument.append(this.get(key));
        return serializableDocument;
    }

    /**
     * Appends whole object
     * @param value
     * @return
     */
    public SerializableDocument append(Object value) {
        this.clear();
        JsonObject jsonObject = new Gson().toJsonTree(value).getAsJsonObject();
        for (String key : jsonObject.keySet()) {
            this.put(key, jsonObject.get(key));
        }
        return this;
    }

    /**
     * Gets value as custom
     * @param key
     * @param t
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> t) {
        return this.toDocument().getObject(key, t);
    }

    /**
     * Gets value as Integer
     * @param key
     * @return
     */
    public Integer getInteger(String key) {
        return (Integer) this.get(key);
    }

    /**
     * Gets value as Boolean
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {
        return (Boolean) this.get(key);
    }

    /**
     * Gets value as Boolean
     * @param key
     * @return
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {
        if (!this.has(key)) {
            return defaultValue;
        }
        return (Boolean) this.get(key);
    }

    /**
     * Gets value as String
     * @param key
     * @return
     */
    public String getString(String key) {
        return (String) this.get(key);
    }

    /**
     * Checks if contains key object
     * @param key
     * @return
     */
    public boolean has(String key) {
        return this.containsKey(key);
    }

    /**
     * Returns from Document.class
     * @param jsonBuilder
     * @return
     */
    public static SerializableDocument fromDocument(JsonBuilder jsonBuilder) {
        SerializableDocument serializableDocument = new SerializableDocument();
        for (String key : jsonBuilder.keys()) {
            serializableDocument.append(key, jsonBuilder.getObject(key));
        }
        return serializableDocument;
    }

    /**
     * Returns Document.class object
     * @return
     */
    public JsonBuilder toDocument() {
        JsonBuilder jsonBuilder = new JsonBuilder();
        for (String s : this.keySet()) {
            jsonBuilder.append(s, this.get(s));
        }
        return jsonBuilder;
    }

    /**
     * ToString
     * @return
     */
    public String toString() {
        return this.toDocument().toString();
    }
}
