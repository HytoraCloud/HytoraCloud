package de.lystx.hytoracloud.driver.utils.json;

import com.google.gson.JsonObject;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

//TODO: DOCUMENTATION
public class JsonData {

    /**
     * Gson constant to (de-)serialize Objects
     */
    public static final Gson GSON = (new GsonBuilder())
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    /**
     * The file of this document
     */
    private File file;

    /**
     * The JsonParser for this document
     */
    private JsonParser parser;

    /**
     * The data of this Document
     */
    private com.google.gson.JsonObject jsonObject;

    /**
     * The current default value
     */
    private Object fallbackValue;

    /**
     * Constructs an Empty Document
     */
    public JsonData() {
        this.jsonObject = new JsonObject();
    }

    /**
     * Constructs a document from File
     */
    public JsonData(File file) {
        this(new com.google.gson.JsonObject(), file, null);
    }

    /**
     * Constructs a document from reader
     */
    public JsonData(Reader reader) {
        this();
        try {
            jsonObject = (com.google.gson.JsonObject) parser.parse(reader);
        } catch (Exception e) {
            jsonObject = new com.google.gson.JsonObject();
        }
    }

    /**
     * Parses a Document from string
     *
     * @param input the string
     */
    public JsonData(String input) {
        this(new com.google.gson.JsonObject(), null, input);
    }

    /**
     * Constructs a Document from existing data
     *
     * @param object the data object
     */
    public JsonData(com.google.gson.JsonObject object) {
        this(object, null, null);
    }

    /**
     * Constructs a Document
     *
     * @param object the provided object
     * @param file the file for it
     * @param input the inputString
     */
    public JsonData(com.google.gson.JsonObject object, File file, String input) {
        this.jsonObject = object;
        this.parser = new JsonParser();
        this.file = file;

        if (file != null && file.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                jsonObject = parser.parse(new BufferedReader(reader)).getAsJsonObject();
            } catch (Exception e) {
                jsonObject = new com.google.gson.JsonObject();
            }
        }

        if (input != null) {
            try {
                jsonObject = (com.google.gson.JsonObject) parser.parse(input);
            } catch (Exception e) {
                jsonObject = new com.google.gson.JsonObject();
            }
        }
    }

    public JsonData(URL url) {
        this();
        try {
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");
            uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            uc.addRequestProperty("Pragma", "no-cache");
            String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            this.jsonObject = new JsonParser().parse(json).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public JsonData append(String key, Object value) {
        if (value != null) {
            try {
                if (value instanceof JsonData) {
                    JsonData document = (JsonData) value;
                    this.jsonObject.add(key, document.getBase());
                } else if (value instanceof String) {
                    this.jsonObject.addProperty(key, (String) value);
                } else if (value instanceof Boolean) {
                    this.jsonObject.addProperty(key, (Boolean) value);
                } else if (value instanceof Number) {
                    this.jsonObject.addProperty(key, (Number) value);
                } else if (value instanceof Character) {
                    this.jsonObject.addProperty(key, (Character) value);
                } else {
                    this.jsonObject.add(key, GSON.toJsonTree(value));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    
    public JsonData append(Object value) {
        if (value == null) {
            return this;
        }
        this.jsonObject = GSON.toJsonTree(value).getAsJsonObject();
        return this;
    }

    
    public void remove(String key) {
        this.jsonObject.remove(key);
    }

    
    public <T> List<T> keySet(Class<T> tClass) {
        List<T> list = new ArrayList<>();
        for (String key : this.keySet()) {
            list.add(this.get(key, tClass));
        }
        return list;
    }

    
    public List<String> keySet() {
        List<String> list = new LinkedList<>();
        for (Map.Entry<String, JsonElement> jsonElementEntry : this.jsonObject.entrySet()) {
            list.add(jsonElementEntry.getKey());
        }
        return list;
    }

    
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

    /**
     * Gets a {@link String} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public String getString(String key) {
        if (!this.jsonObject.has(key)) {
            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }
            return this.fallbackValue != null && this.fallbackValue instanceof String ? (String) this.fallbackValue : null;
        }
        return this.jsonObject.get(key).getAsString();
    }

    /**
     * Gets a {@link Integer} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public int getInteger(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }

            return this.fallbackValue != null && this.fallbackValue instanceof Integer ? (Integer) this.fallbackValue : -1;
        }
        return this.jsonObject.get(key).getAsInt();
    }

    /**
     * Gets a {@link Float} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public float getFloat(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }

            return this.fallbackValue != null && this.fallbackValue instanceof Float ? (Float) this.fallbackValue : -1;
        }
        return this.jsonObject.get(key).getAsFloat();
    }

    /**
     * Gets a {@link Long} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public long getLong(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }

            return this.fallbackValue != null && this.fallbackValue instanceof Long ? (Long) this.fallbackValue : -1;
        }
        return this.jsonObject.get(key).getAsLong();
    }

    /**
     * Gets a {@link Double} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public double getDouble(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }

            return this.fallbackValue != null && this.fallbackValue instanceof Double ? (Double) this.fallbackValue : -1;
        }
        return this.jsonObject.get(key).getAsDouble();
    }

    /**
     * Gets a {@link Short} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public short getShort(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }

            return this.fallbackValue != null && this.fallbackValue instanceof Short ? (Short) this.fallbackValue : -1;
        }
        return this.jsonObject.get(key).getAsShort();
    }

    /**
     * Gets a {@link Byte} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public byte getByte(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }

            return this.fallbackValue != null && this.fallbackValue instanceof Byte ? (Byte) this.fallbackValue : -1;
        }
        return this.jsonObject.get(key).getAsByte();
    }

    /**
     * Gets a {@link Boolean} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public boolean getBoolean(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }

            return this.fallbackValue != null && this.fallbackValue instanceof Boolean ? (Boolean) this.fallbackValue : false;
        }
        return this.jsonObject.get(key).getAsBoolean();
    }

    /**
     * Gets a {@link JsonElement} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public JsonElement getElement(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }

            return this.fallbackValue != null && this.fallbackValue instanceof JsonElement ? (JsonElement) this.fallbackValue : null;
        }
        return this.jsonObject.get(key);
    }

    /**
     * Gets a {@link JsonArray} stored under a given key
     *
     * @param key the key where its stored
     * @return the object or (null if no fallbackValue is defined or the fallbackValue)
     */
    public JsonArray getJsonArray(String key) {
        if (!this.jsonObject.has(key)) {

            if (this.fallbackValue != null) {
                this.append(key, this.fallbackValue);
            }

            return this.fallbackValue != null && this.fallbackValue instanceof JsonArray ? (JsonArray) this.fallbackValue : null;
        }
        return this.jsonObject.get(key).getAsJsonArray();
    }

    /**
     * Sets the default value to fallback if
     * the future-request key value does not exist
     *
     * @param fallbackValue the value
     * @return current data
     */
    public JsonData fallback(Object fallbackValue) {
        this.fallbackValue = fallbackValue;
        return this;
    }

    /**
     * Checks if data contains an object
     * stored under a given key
     * @param key the key
     * @return {@code true} if it has the object | {@code false} if it does not contain the object
     */
    public boolean has(String key) {
        return this.jsonObject.has(key);
    }

    /**
     * Checks if data is empty
     */
    public boolean isEmpty() {
        return this.keySet().isEmpty();
    }

    /**
     * Clears this data
     */
    public void clear() {
        this.keySet().forEach(this::remove);
    }

    
    public com.google.gson.JsonObject getGoogleJsonObject(String key) {
        return this.jsonObject.get(key).getAsJsonObject();
    }

    
    public Object get(String key) {
        return this.jsonObject.get(key);
    }

    /**
     * Gets another sub {@link JsonData} stored under a given key
     *
     * @param key the key
     * @return the object
     */
    public JsonData getData(String key) {
        return new JsonData(this.getGoogleJsonObject(key));
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


    /**
     * Tries to get this whole data as a specified generic object
     *
     * @param tClass the generic-class
     * @param <T> the generic
     * @return object
     */
    public <T> T getAs(Class<T> tClass) {
        return this.get(this.jsonObject, tClass);
    }


    /**
     * Tries to get an object (stored under a key) as a specified generic object
     *
     * @param key the key of the object
     * @param tClass the class of the object you want
     * @return object
     */
    public <T> T get(String key, Class<T> tClass) {
        if (!this.has(key)) {
            return this.fallbackValue != null && this.fallbackValue.getClass().equals(tClass) ? (T) this.fallbackValue : null;
        }
        return this.get(this.getGoogleJsonObject(key), tClass);
    }

    
    public <T> T get(String key, Type type) {
        if (!this.has(key)) {
            return this.fallbackValue != null ? (T) this.fallbackValue : null;
        }
        return GSON.fromJson(this.getGoogleJsonObject(key), type);
    }


    public <T> List<T> getList(String key, Class<T> tClass) {
        if (!this.has(key)) {
            return this.fallbackValue != null && this.fallbackValue instanceof List ? (List<T>) this.fallbackValue : null;
        }
        List<T> tList = new ArrayList<>();
        JsonArray array = this.getJsonArray(key);
        for (JsonElement jsonElement : array) {
            tList.add(GSON.fromJson(jsonElement, tClass));
        }
        return tList;
    }

    
    public <T> List<T> getInterfaceList(String key, Class<T> interfaceClass, Class<? extends T> wrapperObjectClass) {
        if (!this.has(key)) {
            return this.fallbackValue != null && this.fallbackValue instanceof List ? (List<T>) this.fallbackValue : null;
        }
        List<T> tList = new ArrayList<>();
        JsonArray array = this.getJsonArray(key);
        for (JsonElement jsonElement : array) {
            tList.add(GSON.fromJson(jsonElement, wrapperObjectClass));
        }
        return tList;
    }

    
    public List<String> getStringList(String key) {
        if (!this.has(key)) {
            return this.fallbackValue != null && this.fallbackValue instanceof List ? (List<String>) this.fallbackValue : null;
        }
        List<String> list = new LinkedList<>();

        for (JsonElement jsonElement : this.getJsonArray(key)) {
            list.add(jsonElement.getAsString());
        }
        return list;
    }


    /**
     * Saves this data to a {@link File} if the
     * file of this object has been set
     */
    public void save() {
        this.save(this.file);
    }

    /**
     * Saves this data to a {@link File}
     *
     * @param file the file
     */
    public void save(File file) {
        this.file = file;
        try {
            this.save(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Saves this data to an {@link OutputStream}
     *
     * @param outputStream the stream
     */
    public void save(OutputStream outputStream) {
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {
            w.print(GSON.toJson(this.jsonObject));
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the base of this data as {@link JsonElement}
     *
     * @return the data object
     */
    public JsonElement getBase() {
        return this.jsonObject;
    }

    /**
     * Clears and deletes this data
     */
    public void delete() {
        this.clear();
        this.file.delete();
    }


    @Override
    public String toString() {
        return GSON.toJson(this.jsonObject);
    }

}
