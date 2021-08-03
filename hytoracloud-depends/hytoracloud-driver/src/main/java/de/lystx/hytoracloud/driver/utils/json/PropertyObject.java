package de.lystx.hytoracloud.driver.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

@Getter

/*
 * Can be send in packets
 * Map is serializable
 * Document uses Gson which isn't
 */

public class PropertyObject implements JsonObject<PropertyObject> {

    private static final long serialVersionUID = 3815533212648499512L;

    /**
     * The json string for document parsing
     */
    private String jsonString;

    /**
     * The file of this object
     */
    @Getter @Setter
    private File file;

    /**
     * The default value
     */
    private Object defaultValue;

    public PropertyObject() {
        this("{}");
    }

    public PropertyObject(String input) {
        this.jsonString = input;
        this.defaultValue = null;
    }

    public String toString() {
        return new JsonDocument(this.jsonString).toString();
    }

    @Override
    public JsonObject<PropertyObject> append(Object value) {
        return this.modify(document -> document.append(value));
    }

    @Override
    public JsonObject<PropertyObject> append(String key, Object value) {
        return this.modify(document -> document.append(key, value));
    }

    @Override
    public JsonObject<?> getObject(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            JsonObject<?> json = document.getObject(key);
            return JsonObject.serializable(json.toString());
        }


        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }

        return ((defaultValue != null && defaultValue instanceof JsonObject) ? (JsonObject<?>) defaultValue : null);

    }

    @Override
    public JsonObject<PropertyObject> def(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public String getString(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getString(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof String) ? (String) defaultValue : null);
    }

    @Override
    public int getInteger(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getInteger(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof Integer) ? (int) defaultValue : -1);
    }

    @Override
    public short getShort(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getShort(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof Short) ? (short) defaultValue : -1);
    }

    @Override
    public byte getByte(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getByte(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof Byte) ? (byte) defaultValue : -1);
    }

    @Override
    public float getFloat(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getInteger(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof Float) ? (Float) defaultValue : -1);
    }

    @Override
    public long getLong(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getLong(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof Long) ? (long) defaultValue : -1);
    }

    @Override
    public boolean getBoolean(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getBoolean(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof Boolean) ? (boolean) defaultValue : false);
    }

    @Override
    public double getDouble(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getDouble(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof Short) ? (short) defaultValue : -1);
    }

    @Override
    public com.google.gson.JsonObject getGoogleJsonObject(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getGoogleJsonObject(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof com.google.gson.JsonObject) ? (com.google.gson.JsonObject) defaultValue : null);
    }

    @Override
    public JsonArray getJsonArray(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getJsonArray(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof JsonArray) ? (JsonArray) defaultValue : null);
    }

    @Override
    public JsonElement getBase() {
        return new JsonDocument(this.jsonString).getBase();
    }

    @Override
    public JsonElement getElement(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getJsonObject().get(key);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue instanceof JsonElement) ? (JsonElement) defaultValue : null);
    }

    @Override
    public <T> T get(String key, Class<T> typeClass) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.get(key, typeClass);
        }

        if (this.defaultValue != null) {
            this.append(key, this.defaultValue);
        }
        return ((defaultValue != null && defaultValue.getClass().equals(typeClass)) ? (T) defaultValue : null);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> tClass) {
        return new JsonDocument(this.jsonString).getList(key, tClass);
    }

    @Override
    public <T> List<T> getInterfaceList(String key, Class<T> interfaceClass, Class<? extends T> wrapperObjectClass) {
        return new JsonDocument(this.jsonString).getInterfaceList(key, interfaceClass, wrapperObjectClass);
    }

    @Override
    public List<String> getStringList(String key) {
        return new JsonDocument(this.jsonString).getStringList(key);
    }

    @Override
    public List<String> keysExclude(String... strings) {
        return new JsonDocument(this.jsonString).keysExclude(strings);
    }

    @Override
    public <T> List<T> keySet(Class<T> tClass) {
        return new JsonDocument(this.jsonString).keySet(tClass);
    }

    @Override
    public Object get(String key) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.getObject(key);
        }
        return defaultValue != null ? defaultValue : null;
    }

    @Override
    public <T> T getAs(Class<T> typeClass) {
        return new JsonDocument(this.jsonString).getAs(typeClass);
    }

    @Override
    public void delete() {
        new JsonDocument(this.jsonString).delete();
    }


    @Override
    public <T> T get(String key, Type type) {
        JsonDocument document = new JsonDocument(this.jsonString);
        if (document.has(key)) {
            return document.get(key, type);
        }
        return defaultValue != null ? (T) defaultValue : null;
    }

    @Override
    public void remove(String key) {
        this.modify(document -> document.remove(key));
    }

    @Override
    public void save(File file) {
        this.file = file;
        new JsonDocument(this.jsonString).save(file);
    }

    @Override
    public boolean has(String key) {
        return new JsonDocument(this.jsonString).has(key);
    }

    @Override
    public List<String> keySet() {
        return new JsonDocument(this.jsonString).keySet();
    }

    @Override
    public boolean isEmpty() {
        return keySet().isEmpty();
    }

    @Override
    public void clear() {
        this.modify(JsonDocument::clear);
    }

    @Override
    public void save(OutputStream outputStream) {
        new JsonDocument(this.jsonString).save(outputStream);
    }

    /**
     * Modifies the current object
     *
     * @param consumer the consumer
     */
    private JsonObject<PropertyObject> modify(Consumer<JsonDocument> consumer) {
        JsonDocument document = new JsonDocument(this.jsonString);
        consumer.accept(document);
        this.jsonString = document.toString();
        return this;
    }
}