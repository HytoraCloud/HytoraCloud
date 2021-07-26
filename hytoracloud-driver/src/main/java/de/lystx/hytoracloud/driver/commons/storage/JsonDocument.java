package de.lystx.hytoracloud.driver.commons.storage;

import com.google.gson.*;

import de.lystx.hytoracloud.driver.cloudservices.managing.database.impl.DocumentDatabase;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

@Getter @Setter
public class JsonDocument implements Iterable<JsonElement> {

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
     * Parses the object from file
     *
     * @param file the file
     */
    public JsonDocument readFile(String file) {
        File file1 = new File(file);
        if (file1.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                jsonObject = parser.parse(new BufferedReader(reader)).getAsJsonObject();
            } catch (Exception e) {
                jsonObject = new JsonObject();
            }
        }
        return this;
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

    /**
     * Appends am Object to this Document
     *
     * @param key the key where it gets saved
     * @param value the value
     * @return current Document
     */
    public JsonDocument append(String key, Object value) {
        try {
            if (value == null) {
                return this;
            }
            if (value instanceof JsonDocument) {
                JsonDocument document = (JsonDocument)value;
                this.jsonObject.add(key, document.getJsonObject());
            } else {
                this.jsonObject.add(key, GSON.toJsonTree(value));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Appends a whole Value to the document
     *
     * @param value the value to add
     * @return current Document
     */
    public JsonDocument append(Object value) {
        if (value == null) {
            return this;
        }
        this.jsonObject = GSON.toJsonTree(value).getAsJsonObject();
        return this;
    }

    /**
     * Removes an Object from a key
     *
     * @param key the key where the object is stored
     * @return current Document
     */
    public JsonDocument remove(String key) {
        this.jsonObject.remove(key);
        return this;
    }

    /**
     * Loads a list of objects with a given class
     *
     * @param tClass the class you want the objects to be
     * @param <T> generic type
     * @return list of objects
     */
    public <T> List<T> keys(Class<T> tClass) {
        List<T> list = new ArrayList<>();
        for (String key : this.keys()) {
            list.add(this.getObject(key, tClass));
        }
        return list;
    }

    /**
     * Loads the keys of this Document
     *
     * @return list of keys
     */
    public List<String> keys() {
        List<String> list = new LinkedList<>();
        for (Map.Entry<String, JsonElement> jsonElementEntry : this.jsonObject.entrySet()) {
            list.add(jsonElementEntry.getKey());
        }
        return list;
    }
    /**
     * Loads the keys of this Document
     *
     * @return list of keys
     */
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
     * Returns a String by a Key
     *
     * @param key the key where the object is stored
     * @return current Document
     */
    public String getString(String key) {
        if (!this.jsonObject.has(key)) {
            return "ERROR";
        }
        return this.jsonObject.get(key).getAsString();
    }

    /**
     * Returns a String by a Key
     * And appends and returns a default value if not set
     *
     * @param key the key where the object is stored
     * @param value the default Value
     * @return current Document
     */
    public String getString(String key, String value) {
        if (!this.jsonObject.has(key)) {
            this.jsonObject.addProperty(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsString();
    }

    /**
     * Returns an Integer by a Key
     *
     * @param key the key where the object is stored
     * @return current Document
     */
    public int getInteger(String key) {
        if (!this.jsonObject.has(key)) {
            return -1;
        }
        return this.jsonObject.get(key).getAsInt();
    }

    /**
     * Returns a Integer by a Key
     * And appends and returns a default value if not set
     *
     * @param key the key where the object is stored
     * @param value the default Value
     * @return current Document
     */
    public int getInteger(String key, Integer value) {
        if (!this.jsonObject.has(key)) {
            this.jsonObject.addProperty(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsInt();
    }

    /**
     * Returns an float by a Key
     *
     * @param key the key where the object is stored
     * @return current Document
     */
    public double getFloat(String key) {
        if (!this.jsonObject.has(key)) {
            return -1;
        }
        return this.jsonObject.get(key).getAsDouble();
    }

    /**
     * Returns a float by a Key
     * And appends and returns a default value if not set
     *
     * @param key the key where the object is stored
     * @param value the default Value
     * @return current Document
     */
    public double getFloat(String key, double value) {
        if (!this.jsonObject.has(key)) {
            this.jsonObject.addProperty(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsDouble();
    }

    /**
     * Returns an long by a Key
     *
     * @param key the key where the object is stored
     * @return current Document
     */
    public long getLong(String key) {
        if (!this.jsonObject.has(key)) {
            return -1;
        }
        return this.jsonObject.get(key).getAsLong();
    }

    /**
     * Returns a float by a Key
     * And appends and returns a default value if not set
     *
     * @param key the key where the object is stored
     * @param value the default Value
     * @return current Document
     */
    public long getLong(String key, long value) {
        if (!this.jsonObject.has(key)) {
            this.jsonObject.addProperty(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsLong();
    }

    /**
     * Returns a Boolean by a Key
     *
     * @param key the key where the object is stored
     * @return current Document
     */
    public boolean getBoolean(String key) {
        if (!this.jsonObject.has(key)) {
            return false;
        }
        return this.jsonObject.get(key).getAsBoolean();
    }

    /**
     * Returns a Boolean by a Key
     * And appends and returns a default value if not set
     *
     * @param key the key where the object is stored
     * @param value the default Value
     * @return current Document
     */
    public boolean getBoolean(String key, Boolean value) {
        if (!this.jsonObject.has(key)) {
            this.jsonObject.addProperty(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsBoolean();
    }


    /**
     * Returns a {@link JsonArray} by key
     *
     * @param key the key where the array is stored
     * @return array
     */
    public JsonArray getArray(String key) {
        if (!this.jsonObject.has(key)) {
            return null;
        }
        return this.jsonObject.get(key).getAsJsonArray();
    }

    /**
     * Checks if this document has a value
     * stored under this key
     *
     * @param key the provided key
     * @return boolean
     */
    public boolean has(String key) {
        return this.jsonObject.has(key);
    }

    /**
     * Checks if document is empty
     * @return boolean
     */
    public Boolean isEmpty() {
        return this.keys().isEmpty();
    }

    /**
     * Clears this document
     * (Removes every object)
     */
    public void clear() {
        this.keys().forEach(this::remove);
    }

    /**
     * Returns a Sub-Document inside this Document
     * to work with it
     *
     * @param key the key where its stored
     * @return document
     */
    public JsonDocument getJson(String key) {
        return new JsonDocument(this.getJsonObject(key));
    }

    /**
     * Returns a {@link JsonObject} from key
     *
     * @param key the key where its stored
     * @return jsonObject
     */
    public JsonObject getJsonObject(String key) {
        return this.jsonObject.get(key).getAsJsonObject();
    }


    /**
     * Returns a raw Object from key
     * @param key the key
     * @return object
     */
    public Object getObject(String key) {
        return this.jsonObject.get(key);
    }

    /**
     * Returns an Object from a given Class
     *
     * @param jsonObject the source
     * @param tClass the type of what you want to get
     * @return the object
     */
    public <T> T getObject(JsonObject jsonObject, Class<T> tClass) {
        if (jsonObject == null) {
            return null;
        }
        return GSON.fromJson(jsonObject, tClass);
    }

    /**
     * Returns this whole Document as
     * @param tClass the class u want it to be
     * @return the object
     */
    public <T> T getAs(Class<T> tClass) {
        return this.getObject(this.getJsonObject(), tClass);
    }

    /**
     * Returns an Object from a given Class
     *
     * @param key the key where its stored
     * @param tClass the type of what you want to get
     * @return the object
     */
    public <T> T getObject(String key, Class<T> tClass) {
        return this.getObject(this.getJsonObject(key), tClass);
    }
    /**
     * Returns an Object from a given Class
     *
     * @param key the key where its stored
     * @param type the type of what you want to get
     * @return the object
     */
    public <T> T getObject(String key, Type type) {
        return GSON.fromJson(this.getJsonObject(key), type);
    }

    /**
     * Returns a List full of custom objects
     *
     * @param key the key where the list is stored
     * @param tClass the class of the object you want
     * @return list
     */
    public <T> List<T> getList(String key, Class<T> tClass) {
        List<T> tList = new ArrayList<>();
        JsonArray array = this.getArray(key);
        for (JsonElement jsonElement : array) {
            tList.add(GSON.fromJson(jsonElement, tClass));
        }
        return tList;
    }

    /**
     * Returns a list full of strings
     *
     * @param key the key where the list is stored
     * @return list
     */
    public List<String> getStringList(String key) {
        List<String> list = new LinkedList<>();

        for (JsonElement jsonElement : this.getArray(key)) {
            list.add(jsonElement.getAsString());
        }
        return list;
    }

    /**
     * Saves it (only if file is set)
     */
    public void save() {
        this.save(this.file);
    }

    /**
     * Saves this Document to a file
     *
     * @param file the file to save it to
     */
    public void save(File file) {
        this.file = file;
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true)) {
            w.print(GSON.toJson(this.getJsonObject()));
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(OutputStream outputStream) {
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {
            w.print(GSON.toJson(this.getJsonObject()));
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns Document as formatted
     * Json String with PrettyPrinting
     *
     * @return string
     */
    public String toString() {
        return GSON.toJson(this.getJsonObject());
    }

    /**
     * Builds the object
     * @return data
     */
    public JsonObject build() {
        return jsonObject;
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
    /**
     * Transforms a String into a list
     *
     * @param input the input of the string
     * @param listClass the type of the list
     * @return object
     */
    public static <V> List<V> getListFromClass(String input, Class<V> listClass) {
        List<V> list = new LinkedList<>();
        for (JsonElement jsonElement : new JsonDocument(input).getJsonObject().getAsJsonArray()) {
            JsonDocument document = new JsonDocument(jsonElement.toString());
            list.add(document.getAs(listClass));
        }
        return list;
    }

    /**
     * Iterates through the builder with a given class
     *
     * @param tClass the class
     * @param consumer the consumer
     * @param <T> the generic type
     */
    public <T> void forEach(Class<T> tClass, Consumer<T> consumer) {
        List<T> tList = new ArrayList<>();

        for (String key : this.keys()) {
            tList.add(this.getObject(key, tClass));
        }
        tList.forEach(consumer);
    }


    @NotNull
    @Override
    public Iterator<JsonElement> iterator() {
        List<JsonElement> objects = new ArrayList<>();
        for (String key : keys()) {
            objects.add(this.jsonObject.get(key));
        }
        return objects.iterator();
    }


    /**
     * Transforms this entity to a serializable map
     *
     * @return map filled with all objects
     */
    public Map<String, Object> toSerializableMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : this.keys()) {
            map.getOrDefault(key, this.getObject(key));
        }
        return map;
    }

    /**
     * Deletes the current file
     */
    public void delete() {
        this.clear();
        this.file.delete();
    }

    public JsonDocument name(String name) {
        return this.append(DocumentDatabase.NAME_KEY, name);
    }
}
