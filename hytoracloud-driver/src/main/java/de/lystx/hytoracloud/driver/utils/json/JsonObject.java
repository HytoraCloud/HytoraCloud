package de.lystx.hytoracloud.driver.utils.json;

import java.io.File;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

public interface JsonObject<V> extends Serializable {

    static JsonObject<JsonDocument> gson(URL url) {
        return new JsonDocument(url);
    }

    static JsonObject<PropertyObject> serializable() {
        return serializable("{}");
    }

    static JsonObject<PropertyObject> serializable(String input) {
        return new PropertyObject(input);
    }

    static JsonObject<JsonDocument> gson() {
        return gson("{}");
    }

    static JsonObject<JsonDocument> gson(String input) {
        return new JsonDocument(input);
    }

    static JsonObject<JsonDocument> gson(Reader reader) {
        return new JsonDocument(reader);
    }

    /**
     * Transforms the current object
     * into the given object as json
     *
     * @param value the object
     * @return current object
     */
    JsonObject<V> append(Object value);

    /**
     * Puts an object into this object
     * And stores this object under a given key
     *
     * @param key the key
     * @param value the value
     * @return current object
     */
    JsonObject<V> append(String key, Object value);

    /**
     * Gets a {@link JsonObject} stored under key
     *
     * @param key the key
     * @return the object that was found
     */
    JsonObject<?> getObject(String key);

    /**
     * Sets the current default value
     * if the object under a key is not found or null
     * the default value will be used
     * This can only be used for the next entry because
     * then it will be overwritten
     *
     * @param defaultValue the value
     * @return current object
     */
    JsonObject<V> def(Object defaultValue);

    /**
     * Gets an value stored under a key as {@link Integer}
     *
     * @param key the key
     * @return object
     */
    int getInteger(String key);

    /**
     * Gets an value stored under a key as {@link String}
     *
     * @param key the key
     * @return object
     */
    String getString(String key);

    /**
     * Gets an value stored under a key as {@link Long}
     *
     * @param key the key
     * @return object
     */
    long getLong(String key);

    /**
     * Gets an value stored under a key as {@link Float}
     *
     * @param key the key
     * @return object
     */
    float getFloat(String key);

    /**
     * Gets an value stored under a key as {@link Boolean}
     *
     * @param key the key
     * @return object
     */
    boolean getBoolean(String key);

    /**
     * Gets an value stored under a key as {@link Double}
     *
     * @param key the key
     * @return object
     */
    double getDouble(String key);

    /**
     * Gets an value stored under a key as {@link Short}
     *
     * @param key the key
     * @return object
     */
    short getShort(String key);

    /**
     * Gets an value stored under a key as {@link Byte}
     *
     * @param key the key
     * @return object
     */
    byte getByte(String key);

    /**
     * Gets an value stored under a key as {@link com.google.gson.JsonObject}
     *
     * @param key the key
     * @return object
     */
    com.google.gson.JsonObject getGoogleJsonObject(String key);

    /**
     * Gets an value stored under a key as {@link com.google.gson.JsonArray}
     *
     * @param key the key
     * @return object
     */
    com.google.gson.JsonArray getJsonArray(String key);

    /**
     * Gets an value stored under a key as {@link com.google.gson.JsonElement}
     *
     * @param key the key
     * @return object
     */
    com.google.gson.JsonElement getElement(String key);

    /**
     * Returns the base of this object
     *
     * @return json element
     */
    com.google.gson.JsonElement getBase();

    /**
     * Gets an value stored under a key as an object you want
     *
     * @param key the key
     * @return object
     */
    <T> T get(String key, Class<T> typeClass);

    /**
     * Gets an value stored under a key as an object you want
     *
     * @param key the key
     * @return object
     */
    <T> T get(String key, Type type);

    /**
     * Gets this whole object as a given object you want
     *
     * @param typeClass the type class
     * @param <T> the generic
     * @return object
     */
    <T> T getAs(Class<T> typeClass);

    /**
     * Returns a List full of custom objects
     *
     * @param key the key where the list is stored
     * @param tClass the class of the object you want
     * @return list
     */
    <T> List<T> getList(String key, Class<T> tClass);

    /**
     * Returns a List full of custom objects
     *
     * @param key the key where the list is stored
     * @param interfaceClass the class of the interface
     * @param wrapperObjectClass the implementation class
     * @return list
     */
    <T> List<T> getInterfaceList(String key, Class<T> interfaceClass, Class<? extends T> wrapperObjectClass);

    /**
     * Returns a list full of strings
     *
     * @param key the key where the list is stored
     * @return list
     */
    List<String> getStringList(String key);

    /**
     * Gets an object stored under a key
     *
     * @param key the key
     * @return object
     */
    Object get(String key);

    /**
     * A list of all keys
     *
     * @return the entries
     */
    List<String> keySet();

    /**
     * Loads the keys of this Document
     * excluding some keys if their name is one of the provided
     *
     * @return list of keys
     */
    List<String> keysExclude(String... strings);

    /**
     * Loads a list of objects with a given class
     *
     * @param tClass the class you want the objects to be
     * @param <T> generic type
     * @return list of objects
     */
    <T> List<T> keySet(Class<T> tClass);

    /**
     * Removes an entry under a given key
     * @param key the key
     */
    void remove(String key);

    /**
     * Saves this object into a file
     *
     * @param file the file
     */
    void save(File file);

    /**
     * Saves this file to a outputstream
     *
     * @param outputStream the stream
     */
    void save(OutputStream outputStream);

    /**
     * Saves the object into the current file
     *
     * @deprecated because file might be null
     */
    @Deprecated
    default void save() {
        this.save(this.getFile());
    }

    /**
     * Clears the current object
     */
    void clear();

    /**
     * Deletes this object
     */
    void delete();

    /**
     * Gets the file of this object
     */
    File getFile();

    /**
     * Sets the file of this object
     *
     * @param file the file
     */
    void setFile(File file);

    /**
     * Checks if this object has an object
     * stored under a given key
     *
     * @param key the key
     * @return boolean
     */
    boolean has(String key);

    /**
     * Checks if this object is empty
     *
     * @return boolean
     */
    boolean isEmpty();

}
