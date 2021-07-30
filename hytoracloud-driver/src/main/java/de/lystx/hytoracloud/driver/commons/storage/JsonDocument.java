package de.lystx.hytoracloud.driver.commons.storage;

import com.google.gson.*;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter @Setter
public class JsonDocument implements de.lystx.hytoracloud.driver.commons.storage.JsonObject<JsonDocument> {

    /**
     * Gson constant to (de-)serialize Objects
     */
    public static final Gson GSON = (new GsonBuilder()).serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * The file of this document
     */
    private File file;

    /**
     * The JsonParser for this document
     */
    private final JsonParser parser;

    /**
     * The data of this Document
     */
    private JsonObject jsonObject;

    /**
     * The current default value
     */
    private Object defaultValue;

    /**
     * Constructs an Empty Document
     */
    public JsonDocument() {
        this(new JsonObject());
    }

    /**
     * Constructs a document from File
     */
    public JsonDocument(File file) {
        this(new JsonObject(), file, null);
    }

    /**
     * Constructs a document from reader
     */
    public JsonDocument(Reader reader) {
        this();
        try {
            jsonObject = (JsonObject) parser.parse(reader);
        } catch (Exception e) {
            jsonObject = new JsonObject();
        }
    }

    /**
     * Parses a Document from string
     *
     * @param input the string
     */
    public JsonDocument(String input) {
        this(new JsonObject(), null, input);
    }

    /**
     * Constructs a Document from existing data
     *
     * @param object the data object
     */
    public JsonDocument(JsonObject object) {
        this(object, null, null);
    }

    /**
     * Constructs a Document
     *
     * @param object the provided object
     * @param file the file for it
     * @param input the inputString
     */
    public JsonDocument(JsonObject object, File file, String input) {
        this.jsonObject = object;
        this.parser = new JsonParser();
        this.file = file;

        if (file != null && file.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                jsonObject = parser.parse(new BufferedReader(reader)).getAsJsonObject();
            } catch (Exception e) {
                jsonObject = new JsonObject();
            }
        }

        if (input != null) {
            try {
                jsonObject = (JsonObject) parser.parse(input);
            } catch (Exception e) {
                jsonObject = new JsonObject();
            }
        }
    }

    @Override
    public de.lystx.hytoracloud.driver.commons.storage.JsonObject<JsonDocument> append(String key, Object value) {
        try {
            if (value == null) {
                return this;
            }
            if (value instanceof JsonDocument) {
                JsonDocument document = (JsonDocument) value;
                this.jsonObject.add(key, document.getJsonObject());
            } else if (value instanceof PropertyObject) {
                PropertyObject document = (PropertyObject) value;
                this.jsonObject.add(key, parser.parse(document.toString()));
            } else {
                this.jsonObject.add(key, GSON.toJsonTree(value));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public de.lystx.hytoracloud.driver.commons.storage.JsonObject<JsonDocument> append(Object value) {
        if (value == null) {
            return this;
        }
        this.jsonObject = GSON.toJsonTree(value).getAsJsonObject();
        return this;
    }

    @Override
    public void remove(String key) {
        this.jsonObject.remove(key);
    }

    @Override
    public <T> List<T> keySet(Class<T> tClass) {
        List<T> list = new ArrayList<>();
        for (String key : this.keySet()) {
            list.add(this.get(key, tClass));
        }
        return list;
    }

    @Override
    public List<String> keySet() {
        List<String> list = new LinkedList<>();
        for (Map.Entry<String, JsonElement> jsonElementEntry : this.jsonObject.entrySet()) {
            list.add(jsonElementEntry.getKey());
        }
        return list;
    }

    @Override
    public List<String> keysExclude(String... strings) {
        List<String> list = new LinkedList<>();
        for (Map.Entry<String, JsonElement> jsonElementEntry : this.jsonObject.entrySet()) {
            boolean b = false;
            for (String string : strings) {
                if (jsonElementEntry.getKey().equalsIgnoreCase(string)) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                list.add(jsonElementEntry.getKey());
            }
        }
        return list;
    }

    @Override
    public String getString(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.defaultValue != null) {
                this.append(key, this.defaultValue);
            }

            return this.defaultValue != null && this.defaultValue instanceof String ? (String) this.defaultValue : null;
        }
        return this.jsonObject.get(key).getAsString();
    }

    @Override
    public int getInteger(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.defaultValue != null) {
                this.append(key, this.defaultValue);
            }

            return this.defaultValue != null && this.defaultValue instanceof Integer ? (Integer) this.defaultValue : -1;
        }
        return this.jsonObject.get(key).getAsInt();
    }

    @Override
    public float getFloat(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.defaultValue != null) {
                this.append(key, this.defaultValue);
            }

            return this.defaultValue != null && this.defaultValue instanceof Float ? (Float) this.defaultValue : -1;
        }
        return this.jsonObject.get(key).getAsFloat();
    }

    @Override
    public long getLong(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.defaultValue != null) {
                this.append(key, this.defaultValue);
            }

            return this.defaultValue != null && this.defaultValue instanceof Long ? (Long) this.defaultValue : -1;
        }
        return this.jsonObject.get(key).getAsLong();
    }

    @Override
    public double getDouble(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.defaultValue != null) {
                this.append(key, this.defaultValue);
            }

            return this.defaultValue != null && this.defaultValue instanceof Double ? (Double) this.defaultValue : -1;
        }
        return this.jsonObject.get(key).getAsDouble();
    }

    @Override
    public boolean getBoolean(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.defaultValue != null) {
                this.append(key, this.defaultValue);
            }

            return this.defaultValue != null && this.defaultValue instanceof Boolean ? (Boolean) this.defaultValue : false;
        }
        return this.jsonObject.get(key).getAsBoolean();
    }

    @Override
    public JsonElement getElement(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.defaultValue != null) {
                this.append(key, this.defaultValue);
            }

            return this.defaultValue != null && this.defaultValue instanceof JsonElement ? (JsonElement) this.defaultValue : null;
        }
        return this.jsonObject.get(key);
    }

    @Override
    public JsonArray getJsonArray(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.defaultValue != null) {
                this.append(key, this.defaultValue);
            }

            return this.defaultValue != null && this.defaultValue instanceof JsonArray ? (JsonArray) this.defaultValue : null;
        }
        return this.jsonObject.get(key).getAsJsonArray();
    }


    @Override
    public de.lystx.hytoracloud.driver.commons.storage.JsonObject<JsonDocument> def(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public boolean has(String key) {
        return this.jsonObject.has(key);
    }

    @Override
    public boolean isEmpty() {
        return this.keySet().isEmpty();
    }

    @Override
    public void clear() {
        this.keySet().forEach(this::remove);
    }

    @Override
    public JsonObject getGoogleJsonObject(String key) {
        return this.jsonObject.get(key).getAsJsonObject();
    }

    @Override
    public Object get(String key) {
        return this.jsonObject.get(key);
    }

    @Override
    public de.lystx.hytoracloud.driver.commons.storage.JsonObject<?> getObject(String key) {
        return de.lystx.hytoracloud.driver.commons.storage.JsonObject.gson(this.getGoogleJsonObject(key).toString());
    }

    /**
     * Returns an Object from a given Class
     *
     * @param jsonObject the source
     * @param tClass the type of what you want to get
     * @return the object
     */
    public <T> T get(JsonObject jsonObject, Class<T> tClass) {
        if (jsonObject == null) {
            return null;
        }
        return GSON.fromJson(jsonObject, tClass);
    }

    @Override
    public <T> T getAs(Class<T> tClass) {
        return this.get(this.getJsonObject(), tClass);
    }

    @Override
    public <T> T get(String key, Class<T> tClass) {
        if (!this.has(key)) {
            return this.defaultValue != null && this.defaultValue.getClass().equals(tClass) ? (T) this.defaultValue : null;
        }
        return this.get(this.getGoogleJsonObject(key), tClass);
    }

    @Override
    public <T> T get(String key, Type type) {
        if (!this.has(key)) {
            return this.defaultValue != null ? (T) this.defaultValue : null;
        }
        return GSON.fromJson(this.getGoogleJsonObject(key), type);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> tClass) {
        if (!this.has(key)) {
            return this.defaultValue != null && this.defaultValue instanceof List ? (List<T>) this.defaultValue : null;
        }
        List<T> tList = new ArrayList<>();
        JsonArray array = this.getJsonArray(key);
        for (JsonElement jsonElement : array) {
            tList.add(GSON.fromJson(jsonElement, tClass));
        }
        return tList;
    }

    @Override
    public List<String> getStringList(String key) {
        if (!this.has(key)) {
            return this.defaultValue != null && this.defaultValue instanceof List ? (List<String>) this.defaultValue : null;
        }
        List<String> list = new LinkedList<>();

        for (JsonElement jsonElement : this.getJsonArray(key)) {
            list.add(jsonElement.getAsString());
        }
        return list;
    }


    @Override
    public void save() {
        this.save(this.file);
    }

    @Override
    public void save(File file) {
        this.file = file;
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true)) {
            w.print(GSON.toJson(this.jsonObject));
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void save(OutputStream outputStream) {
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {
            w.print(GSON.toJson(this.jsonObject));
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete() {
        this.clear();
        this.file.delete();
    }

    @Override
    public String toString() {
        return GSON.toJson(this.jsonObject);
    }

    /**
     * Formats an Object directly
     * into a formatted JsonString
     *
     * @param src the object to transform
     * @return string as json
     */
    public static String toString(Object src) {
        return GSON.toJson(src);
    }

    /**
     * Transforms a String into the object you want
     *
     * @param input the input of the string
     * @param tClass the class of the object you want
     * @return object
     */
    public static <T> T fromClass(String input, Class<T> tClass) {
        return new JsonDocument(input).getAs(tClass);
    }

}
