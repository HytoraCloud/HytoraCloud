package utillity;

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
public class PropertyObject extends HashMap<String, Object> implements Serializable {

    public PropertyObject() {

    }

    public PropertyObject(String input) {
        JsonEntity jsonEntity = new JsonEntity(input);

        for (String key : jsonEntity.keys()) {
            this.append(key, jsonEntity.getObject(key));
        }

    }

    /**
     * Adds value
     * @param key
     * @param value
     * @return
     */
    public PropertyObject append(String key, Object value) {
        if (value instanceof JsonEntity) {
            this.put(key, ((JsonEntity) value).getJsonObject());
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
     * @param jsonEntity
     * @return
     */
    public static PropertyObject fromDocument(JsonEntity jsonEntity) {
        PropertyObject propertyObject = new PropertyObject();
        for (String key : jsonEntity.keys()) {
            propertyObject.append(key, jsonEntity.getObject(key));
        }
        return propertyObject;
    }

    /**
     * Returns Document.class object
     * @return
     */
    public JsonEntity toDocument() {
        JsonEntity jsonEntity = new JsonEntity();
        for (String s : this.keySet()) {
            jsonEntity.append(s, this.get(s));
        }
        return jsonEntity;
    }

    /**
     * ToString
     * @return
     */
    public String toString() {
        return this.toDocument().toString();
    }
}
