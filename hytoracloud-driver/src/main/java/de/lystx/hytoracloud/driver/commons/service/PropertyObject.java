package de.lystx.hytoracloud.driver.commons.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter

/*
 * Can be send in packets
 * Map is serializable
 * Document uses Gson which isn't
 */
public class PropertyObject extends HashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 3815533212648499512L;

    public PropertyObject() {

    }

    public PropertyObject(String input) {
        JsonDocument jsonDocument = new JsonDocument(input);

        for (String key : jsonDocument.keys()) {
            this.append(key, jsonDocument.getObject(key));
        }

    }

    /**
     * Adds value
     * @param key
     * @param value
     * @return
     */
    public PropertyObject append(String key, Object value) {
        if (value instanceof JsonDocument) {
            JsonDocument document = (JsonDocument)value;
            this.put(key, document.build());
        } else if (value instanceof PropertyObject) {
            this.put(key, value);
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
    public PropertyObject getDocument(String key) {
        PropertyObject propertyObject = new PropertyObject();
        propertyObject.append(this.get(key));
        return propertyObject;
    }

    /**
     * Appends whole object
     * @param value
     * @return
     */
    public PropertyObject append(Object value) {
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
        Object o = this.get(key);
        if (o instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive)o;
            return primitive.getAsInt();
        }
        return (Integer) o;
    }

    /**
     * Gets value as long
     * @param key
     * @return
     */
    public long getLong(String key) {
        Object o = this.get(key);
        if (o instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive)o;
            return primitive.getAsLong();
        }
        if (o instanceof Double) {
            return Long.parseLong(String.valueOf(o).split("\\.")[0]);
        }
        return Long.parseLong("" + o);
    }

    /**
     * Gets value as double
     * @param key
     * @return
     */
    public double getDouble(String key) {
        Object o = this.get(key);
        if (o instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive)o;
            return primitive.getAsDouble();
        }
        return (double) o;
    }

    /**
     * Gets value as Boolean
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {
        Object o = this.get(key);
        if (o instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive)o;
            return primitive.getAsBoolean();
        }
        return (Boolean) o;
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
        Object o = this.get(key);
        if (o instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive)o;
            return primitive.getAsString();
        }
        return (String) o;
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
     * @param jsonDocument
     * @return
     */
    public static PropertyObject fromDocument(JsonDocument jsonDocument) {
        PropertyObject propertyObject = new PropertyObject();
        for (String key : jsonDocument.keys()) {
            propertyObject.append(key, jsonDocument.getObject(key));
        }
        return propertyObject;
    }

    /**
     * Returns Document.class object
     * @return
     */
    public JsonDocument toDocument() {
        JsonDocument jsonDocument = new JsonDocument();
        for (String s : this.keySet()) {
            jsonDocument.append(s, this.get(s));
        }
        return jsonDocument;
    }

    /**
     * ToString
     * @return
     */
    public String toString() {
        return this.toDocument().toString();
    }
}
