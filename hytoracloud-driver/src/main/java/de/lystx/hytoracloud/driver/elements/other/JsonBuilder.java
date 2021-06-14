package de.lystx.hytoracloud.driver.elements.other;

import com.google.gson.*;
import io.thunder.packet.PacketBuffer;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter @Setter
public class JsonBuilder {

    /**
     * Gson constant to (de-)serialize Objects
     */
    public static final Gson GSON = (new GsonBuilder()).serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * The file of this document
     */
    private final File file;

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
    public JsonBuilder() {
        this(new JsonObject());
    }

    /**
     * Constructs a document from File
     */
    public JsonBuilder(File file) {
        this(new JsonObject(), file, null);
    }

    /**
     * Constructs a document from reader
     */
    public JsonBuilder(Reader reader) {
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
    public JsonBuilder readFile(String file) {
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
    public JsonBuilder(String input) {
        this(new JsonObject(), null, input);
    }

    /**
     * Constructs a Document from existing data
     *
     * @param object the data object
     */
    public JsonBuilder(JsonObject object) {
        this(object, null, null);
    }

    /**
     * Constructs a Document
     *
     * @param object the provided object
     * @param file the file for it
     * @param input the inputString
     */
    public JsonBuilder(JsonObject object, File file, String input) {
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
    public JsonBuilder append(String key, Object value) {
        try {
            if (value == null) {
                return this;
            }
            if (value instanceof JsonBuilder) {
                this.jsonObject.add(key, ((JsonBuilder) value).getJsonObject());
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
    public JsonBuilder append(Object value) {
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
    public JsonBuilder remove(String key) {
        this.jsonObject.remove(key);
        return this;
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
    public JsonBuilder getJson(String key) {
        return new JsonBuilder(this.getJsonObject(key));
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
     * Formats an Object directly
     * into a formatted JsonString and writes it to a buffer
     *
     * @param src the object to transform
     * @return string as json
     */
    public static String toBuffer(PacketBuffer buf, Object src) {
        String string = toString(src);
        buf.writeString(string);
        return string;
    }

    /**
     * Transforms a String into the object you want
     *
     * @param input the input of the string
     * @param tClass the class of the object you want
     * @return object
     */
    public static <T> T fromClass(String input, Class<T> tClass) {
        return new JsonBuilder(input).getAs(tClass);
    }

}
